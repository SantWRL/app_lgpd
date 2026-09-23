// ═══════════════ Sincronização de vídeos educacionais (agendado no Supabase) ═══════════════
// Usa o RSS público do YouTube (sem API key) para descobrir vídeos de canais
// oficiais sobre LGPD/proteção de dados e faz upsert em public.educational_videos.
//
// Agendamento sugerido (1x por dia):
//   select cron.schedule('sync-videos', '30 6 * * *', $$
//     select net.http_post(
//       url    := 'https://<PROJECT_REF>.functions.supabase.co/sync-videos',
//       headers:= '{"Authorization": "Bearer <SERVICE_ROLE_KEY>"}'::jsonb
//     );
//   $$);
import { createClient } from 'jsr:@supabase/supabase-js@2'

// Canais oficiais: ANPD, e buscas temáticas via RSS do YouTube
const FEEDS: Array<{ url: string; topic: string }> = [
  { url: 'https://www.youtube.com/feeds/videos.xml?channel_id=UCjDEDHbA4L1STU3MqOsD_SQ', topic: 'ANPD' },
  {
    url: 'https://www.youtube.com/feeds/videos.xml?search_query=LGPD+prote%C3%A7%C3%A3o+de+dados+aula',
    topic: 'Aulas',
  },
  {
    url: 'https://www.youtube.com/feeds/videos.xml?search_query=LGPD+explica%C3%A7%C3%A3o',
    topic: 'Conceitos',
  },
]

interface FeedEntry {
  videoId: string
  title: string
  author: string
  published: string
  description: string
}

/** Parser mínimo de XML para o feed RSS do YouTube. */
function parseFeed(xml: string): FeedEntry[] {
  const entries: FeedEntry[] = []
  const entryRegex = /<entry>([\s\S]*?)<\/entry>/g
  let entryMatch: RegExpExecArray | null

  while ((entryMatch = entryRegex.exec(xml)) !== null) {
    const block = entryMatch[1]
    const videoId = block.match(/<yt:videoId>([^<]+)<\/yt:videoId>/)?.[1]
    const title = block.match(/<title>([\s\S]*?)<\/title>/)?.[1]
    const author = block.match(/<author>[\s\S]*?<name>([\s\S]*?)<\/name>/)?.[1]
    const published = block.match(/<published>([^<]+)<\/published>/)?.[1]
    const description =
      block
        .match(/<media:description>([\s\S]*?)<\/media:description>/)?.[1]
        ?.slice(0, 500) ?? ''

    if (videoId && title) {
      entries.push({
        videoId,
        title: title.trim(),
        author: author?.trim() ?? 'YouTube',
        published: published ?? new Date().toISOString(),
        description,
      })
    }
  }
  return entries
}

/** Compara em tempo constante para evitar timing attack. */
function tokenEquals(a: string, b: string): boolean {
  if (a.length !== b.length) return false
  let diff = 0
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i)
  return diff === 0
}

Deno.serve(async (req) => {
  // Só aceita a service role key (a anon key vem no app público e NÃO deve
  // conseguir disparar os scrapers sob demanda).
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
    let synced = 0
    let total = 0
    const errors: string[] = []

    for (const feed of FEEDS) {
      const resp = await fetch(feed.url, {
        headers: { Accept: 'application/xml' },
      })
      if (!resp.ok) continue // canal indisponível não deve quebrar a rodada

      const entries = parseFeed(await resp.text())
      total += entries.length
      if (entries.length === 0) continue

      // Upsert em lote: uma única chamada por feed (em vez de 1 request/vídeo)
      const rows = entries.map((entry) => ({
        video_id: entry.videoId,
        title: entry.title,
        channel_title: entry.author,
        description: entry.description,
        lesson_topic: feed.topic,
        published_at: entry.published,
      }))
      const { error } = await supabase
        .from('educational_videos')
        .upsert(rows, { onConflict: 'video_id' })
      if (error) {
        errors.push(`[${feed.topic}] ${error.message}`)
      } else {
        synced += rows.length
      }
    }

    const status = errors.length > 0 && synced === 0 ? 500 : 200
    return new Response(JSON.stringify({ total, synced, errors }), {
      status,
      headers: { 'Content-Type': 'application/json' },
    })
  } catch (e) {
    return new Response(JSON.stringify({ error: String(e) }), {
      status: 500,
      headers: { 'Content-Type': 'application/json' },
    })
  }
})
