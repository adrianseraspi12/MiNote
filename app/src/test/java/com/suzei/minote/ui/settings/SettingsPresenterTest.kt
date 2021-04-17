package com.suzei.minote.ui.settings

import android.content.SharedPreferences
import com.suzei.minote.BaseUnitTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SettingsPresenterTest : BaseUnitTest() {

    private lateinit var view: SettingsContract.View
    private lateinit var presenter: SettingsPresenter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sharedPrefsEdit: SharedPreferences.Editor

    @Before
    fun setup() {
        sharedPrefs = mock(SharedPreferences::class.java)
        view = mock(SettingsFragment::class.java)
        sharedPrefsEdit = mock(SharedPreferences.Editor::class.java)
        presenter = SettingsPresenter(view, sharedPrefs)
    }

    @Test
    fun createPresenter() {
        verify(view).setPresenter(presenter)
    }

    @Test
    fun test_When_AutoSaveIsCheck_Should_ReturnTrue() {
        `when`(sharedPrefs.getBoolean("auto_save", false)).thenReturn(true)
        presenter.setup()
        verify(view).setDetails(true)
    }

    @Test
    fun test_When_AutoSaveIsNotCheck_Should_ReturnFalse() {
        `when`(sharedPrefs.getBoolean("auto_save", false)).thenReturn(false)
        presenter.setup()
        verify(view).setDetails(false)
    }

    @Test
    fun test_When_AutoSaveIsCheck_Should_ShowToastMessage() {
        `when`(sharedPrefs.edit()).thenReturn(sharedPrefsEdit)
        `when`(sharedPrefsEdit.putBoolean("auto_save", true)).thenReturn(sharedPrefsEdit)
        presenter.setAutoSave(true)
        verify(view).showToastMessage("Auto save is enabled.")
        verify(sharedPrefsEdit.putBoolean("auto_save", true)).apply()
    }

    @Test
    fun test_When_AutoSaveIsNotCheck_Should_SaveInSharedPrefs() {
        `when`(sharedPrefs.edit()).thenReturn(sharedPrefsEdit)
        `when`(sharedPrefsEdit.putBoolean("auto_save", false)).thenReturn(sharedPrefsEdit)
        presenter.setAutoSave(false)
        verify(sharedPrefsEdit.putBoolean("auto_save", false)).apply()
    }
}