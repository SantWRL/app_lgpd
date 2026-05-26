package br.ufpi.lgpd.educacional.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.ufpi.lgpd.educacional.R
import br.ufpi.lgpd.educacional.databinding.ActivityOnboardingBinding
import br.ufpi.lgpd.educacional.ui.MainActivity
import br.ufpi.lgpd.educacional.util.UserPreferences
import com.google.android.material.tabs.TabLayoutMediator

/**
 * OnboardingActivity - Tela de apresentação e orientação inicial.
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(this)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = OnboardingPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            goToHome()
        }

        binding.btnNext.setOnClickListener {
            val nextPage = binding.viewPager.currentItem + 1
            if (nextPage < 5) {
                binding.viewPager.currentItem = nextPage
            } else {
                goToHome()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(
            object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.btnNext.text = if (position == 4) {
                        getString(R.string.onboarding_get_started)
                    } else {
                        getString(R.string.button_continue)
                    }
                }
            }
        )
    }

    private fun goToHome() {
        userPreferences.hasSeenOnboarding = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    inner class OnboardingPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int) = when (position) {
            0 -> OnboardingPageFragment.newInstance(
                getString(R.string.onboarding_welcome_title),
                getString(R.string.onboarding_welcome_subtitle),
                R.drawable.ic_home
            )
            1 -> OnboardingPageFragment.newInstance(
                getString(R.string.onboarding_lgpd_title),
                getString(R.string.onboarding_lgpd_subtitle),
                R.drawable.ic_shield_lock
            )
            2 -> OnboardingPageFragment.newInstance(
                getString(R.string.onboarding_privacy_title),
                getString(R.string.onboarding_privacy_subtitle),
                R.drawable.ic_profile
            )
            3 -> OnboardingPageFragment.newInstance(
                getString(R.string.onboarding_rights_title),
                getString(R.string.onboarding_rights_subtitle),
                R.drawable.ic_book
            )
            4 -> OnboardingPageFragment.newInstance(
                getString(R.string.onboarding_start_title),
                getString(R.string.onboarding_start_subtitle),
                R.drawable.ic_quiz
            )
            else -> OnboardingPageFragment()
        }
    }
}
