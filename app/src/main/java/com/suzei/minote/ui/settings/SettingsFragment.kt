package com.suzei.minote.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.suzei.minote.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(), SettingsContract.View {

    private var _presenter: SettingsContract.Presenter? = null
    private val presenter get() = _presenter!!

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setup()
        setupBackButton()
        setupSwitch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _presenter = null
    }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this._presenter = presenter
    }

    override fun setDetails(isCheck: Boolean) {
        binding.settingsSwitchAutoSave.isChecked = isCheck
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupSwitch() {
        binding.settingsSwitchAutoSave.setOnCheckedChangeListener { _, isCheck ->
            presenter.setAutoSave(isCheck)
        }
    }

    private fun setupBackButton() {
        binding.settingsBackArrow.setOnClickListener { activity?.finish() }
    }
}