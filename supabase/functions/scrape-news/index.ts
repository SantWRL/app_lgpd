// ═══════════════ Scrap de notícias da ANPD (agendado no Supabase) ═══════════════
// O portal gov.br/anpd carrega as notícias via JavaScript, então um scrap direto
// do HTML falha. Esta function usa a API pública de busca do gov.br (que retorna
// HTML server-side com os resultados indexados) e faz upsert em public.news_articles.
//
// Agendamento sugerido (Dashboard → Edge Functions → schedules):
//   select cron.schedule('scrape-news', '0 * * * *', $$
//     select net.http_post(
//       url    := 'https://<PROJECT_REF>.functions.supabase.co/scrape-news',
//       headers:= '{"Authorization": "Bearer <SERVICE_ROLE_KEY>"}'::jsonb
//     );
//   $$);
import { createClient } from 'jsr:@supabase/supabase-js@2'

const GOV_BR_SEARCH_URL =
  'https://www.gov.br/busca/resultado?q=not%C3%ADcia+anpd&sort=published'

const CATEGORY_RULES: Array<{ match: RegExp; category: string }> = [
  { match: /sanci|multa|fiscaliz|notifica/i, category: 'Fiscalização' },
  { match: /titular|direito|consentimento/i, category: 'Direitos' },
  { match: /internacional|conven|coopera|gdpr/i, category: 'Internacional' },
  { match: /educa|campanha|conscientiza|guia/i, category: 'Educação' },
]

function guessCategory(text: string): string {
  for (const rule of CATEGORY_RULES) {
    if (rule.match.test(text)) return rule.category
  }
  return 'Regulatório'
}

function decodeEntities(text: string): string {
  return text
    .replace(/&amp;/g, '&')
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&nbsp;/g, ' ')
    .trim()
}

/** Extrai os resultados da busca do gov.br (HTML server-side). */
function parseResults(html: string): Array<{ title: string; url: string; context: string }> {
  const results: Array<{ title: string; url: string; context: string }> = []
  // Links de notícias do portal da ANPD dentro dos resultados da busca
  const linkRegex =
    /<a[^>]+href="(https:\/\/www\.gov\.br\/anpd\/[^"]+)"[^>]*>([\s\S]*?)<\/a>([\s\S]{0,400})/g
  let match: RegExpExecArray | null
  while ((match = linkRegex.exec(html)) !== null) {
    const url = match[1]
    const title = decodeEntities(match[2].replace(/<[^>]*>/g, ' '))
    // Texto ao redor do link (trecho/resumo) costuma conter a data de publicação
    const context = decodeEntities(match[3].replace(/<[^>]*>/g, ' '))
    if (
      title.length > 20 &&
      url.includes('/pt-br/') &&
      !results.some((r) => r.url === url)
    ) {
      results.push({ title, url, context })
    }
  }
  return results.slice(0, 20)
}

/** Compara em tempo constante para evitar timing attack. */
function tokenEquals(a: string, b: string): boolean {
  if (a.length !== b.length) return false
  let diff = 0
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i)
  return diff === 0
}

/** Tenta extrair a data da publicação do próprio resultado da busca. */
function extractDate(context: string): string | null {
  // gov.br costuma renderizar datas como "12/09/2026" ou "12 de set de 2026"
  const brDate = context.match(/(\d{2})\/(\d{2})\/(\d{4})/)
  if (brDate) {
    const [, d, m, y] = brDate
    const iso = new Date(Date.UTC(+y, +m - 1, +d)).toISOString()
    // Ignora datas futuras (layout trocado, ex. horário 12/09 às 20:26)
    return iso <= new Date().toISOString() ? iso : null
  }
  const months: Record<string, number> = {
    jan: 0, fev: 1, mar: 2, abr: 3, mai: 4, jun: 5,
    jul: 6, ago: 7, set: 8, out: 9, nov: 10, dez: 11,
  }
  const extDate = context.match(/(\d{1,2}) de (\w{3}) de (\d{4})/i)
  if (extDate) {
    const month = months[extDate[2].toLowerCase()]
    if (month !== undefined) {
      const iso = new Date(Date.UTC(+extDate[3], month, +extDate[1])).toISOString()
      return iso <= new Date().toISOString() ? iso : null
    }
  }
  return null
}

Deno.serve(async (req) => {
  // Só aceita a service role key (a anon key vem no app público e NÃO deve
  // conseguir disparar o scrap sob demanda).
  const auth = req.headers.get('Authorization') ?? ''
  const token = auth.startsWith('Bearer ') ? auth.slice(7).trim() : ''
  const serviceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
  if (serviceKey === '' || !tokenEquals(token, serviceKey)) {
    return new Response(JSON.stringify({ error: 'unauthorized' }), {
      status: 401,
      headers: { 'Content-Type': 'application/json' },
    })
  }

  const supabase = createClient(
    Deno.env.get('SUPABASE_URL') ?? '',
    serviceKey
  )

  try {
    const resp = await fetch(GOV_BR_SEARCH_URL, {
      headers: {
        'User-Agent':
          'Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Mobile Safari/537.36',
        Accept: 'text/html',
      },
    })
    if (!resp.ok) throw new Error(`gov.br respondeu ${resp.status}`)

    const html = await resp.text()
    const items = parseResults(html)
    if (items.length === 0) {
      return new Response(JSON.stringify({ inserted: 0, found: 0 }), {
        headers: { 'Content-Type': 'application/json' },
      })
    }

    let inserted = 0
    const errors: string[] = []
    for (const item of items) {
      const publishedAt = extractDate(`${item.title} ${item.context}`)
      const { error } = await supabase.from('news_articles').upsert(
        {
          source: 'anpd',
          title: item.title,
          summary:
            item.title.length > 180
              ? item.title.slice(0, 180) + '…'
              : item.title,
          url: item.url,
          category: guessCategory(item.title),
          author_name: 'ANPD Oficial',
          // Data real quando o HTML traz; senão agora (menos enganoso que
          // sempre "agora", que reordena o feed a cada rodada).
          ...(publishedAt ? { published_at: publishedAt } : {}),
        },
        { onConflict: 'url' }
      )
      if (error) {
        errors.push(error.message)
      } else {
        inserted++
      }
    }

    return new Response(JSON.stringify({ found: items.length, inserted, errors }), {
      status: errors.length > 0 && inserted === 0 ? 500 : 200,
      headers: { 'Content-Type': 'application/json' },
    })
  } catch (e) {
    return new Response(JSON.stringify({ error: String(e) }), {
      status: 500,
      headers: { 'Content-Type': 'application/json' },
    })
  }
})
