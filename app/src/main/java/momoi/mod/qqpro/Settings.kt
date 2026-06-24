package momoi.mod.qqpro

import android.content.SharedPreferences
import androidx.core.content.edit
import momoi.mod.qqpro.util.Utils

object Settings {
    val sp: SharedPreferences = Utils.application.getSharedPreferences("qqpro", 0)
    val scale = FloatPref("scale", 0.72f, 0.9f)
    val chatScale = FloatPref("chatScale", 0.49f, 0.93f)
    val enableSmoothScroll = BooleanPref("enableSmoothScroll", false)
    val blockBack = BooleanPref("blockBack", false)
    val swapCenterKeyboard = BooleanPref("swapCenterKeyboard", false)

    private val moye = Utils.application.getSharedPreferences("wearqq", 0)
    val text get() = moye.getString("voice_btn_text", "")?.let {
        if (it == "QQ") {
            ""
        } else {
            it
        }
    } ?: ""
}

abstract class Pref<T>(def: T) {
    var value: T = def
        set(value) {
            field = value
            set(value)
        }

    protected abstract fun set(value: T)
}

class FloatPref(
    private val key: String,
    def: Float,
    legacyDef: Float? = null
) : Pref<Float>(Settings.sp.getFloat(key, def).let { stored ->
    if (legacyDef != null && stored == legacyDef) def else stored
}) {
    init {
        if (legacyDef != null && Settings.sp.getFloat(key, value) == legacyDef) {
            set(value)
        }
    }

    override fun set(value: Float) = Settings.sp.edit {
        putFloat(key, value)
    }
}

class BooleanPref(private val key: String, def: Boolean) :
    Pref<Boolean>(Settings.sp.getBoolean(key, def)) {
    override fun set(value: Boolean) = Settings.sp.edit {
        putBoolean(key, value)
    }
}