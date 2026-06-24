package momoi.mod.qqpro.hook.style

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.tencent.watch.ime.InputMethodFragment
import momoi.anno.mixin.Mixin
import momoi.mod.qqpro.forEachAll
import momoi.mod.qqpro.lib.dp

@Mixin
class 输入页透明 : InputMethodFragment() {
    private var keyboardGuardRoot: View? = null
    private var keyboardGuardListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var oldSoftInputMode: Int? = null

    override fun Y(p1: LayoutInflater?, p2: ViewGroup?, p3: Bundle?): View? {
        return super.Y(p1, p2, p3)?.apply {
            setBackgroundColor(0x77_000000)
            guardSystemKeyboard()
        }
    }

    override fun onResume() {
        super.onResume()
        applyInputWindowMode()
        keyboardGuardRoot?.let { root ->
            root.post {
                keepActionsAboveKeyboard(root)
            }
        }
    }

    override fun onDestroy() {
        removeKeyboardGuard()
        oldSoftInputMode?.let { activity?.window?.setSoftInputMode(it) }
        super.onDestroy()
    }

    private fun View.guardSystemKeyboard() {
        removeKeyboardGuard()
        keyboardGuardRoot = this
        applyInputWindowMode()
        keyboardGuardListener = ViewTreeObserver.OnGlobalLayoutListener {
            keepActionsAboveKeyboard(this)
        }
        viewTreeObserver.addOnGlobalLayoutListener(keyboardGuardListener)
        post {
            keepActionsAboveKeyboard(this)
        }
    }

    private fun applyInputWindowMode() {
        val window = activity?.window ?: return
        if (oldSoftInputMode == null) {
            oldSoftInputMode = window.attributes.softInputMode
        }
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    private fun keepActionsAboveKeyboard(root: View) {
        val input = findInputText(root)
        val actions = findInputActions(root) ?: return
        actions.visibility = View.VISIBLE
        actions.bringToFront()
        actions.translationY = 0f

        val keyboardHeight = getKeyboardHeight(root)
        val naturalTop = input?.let { it.y + it.height + 10.dp } ?: actions.y
        val maxBottom = if (keyboardHeight > 80.dp) {
            root.height - keyboardHeight - 8.dp
        } else {
            root.height - 8.dp
        }
        val targetTop = naturalTop.coerceAtMost((maxBottom - actions.height).toFloat())
        actions.y = targetTop.coerceAtLeast(0f)
    }

    private fun getKeyboardHeight(root: View): Int {
        val rootLocation = IntArray(2)
        root.getLocationOnScreen(rootLocation)
        val rootBottom = rootLocation[1] + root.height

        val visible = Rect()
        root.getWindowVisibleDisplayFrame(visible)
        val visibleBlocked = (rootBottom - visible.bottom).coerceAtLeast(0)

        val insetBlocked = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.rootWindowInsets?.getInsets(WindowInsets.Type.ime())?.bottom ?: 0
        } else {
            0
        }

        return maxOf(visibleBlocked, insetBlocked)
    }

    private fun removeKeyboardGuard() {
        val root = keyboardGuardRoot
        val listener = keyboardGuardListener
        if (root != null && listener != null && root.viewTreeObserver.isAlive) {
            root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
        keyboardGuardRoot = null
        keyboardGuardListener = null
    }

    private fun findInputText(root: View): View? {
        val group = root as? ViewGroup ?: return null
        var result: View? = null
        group.forEachAll { child ->
            val text = (child as? TextView)?.text?.toString() ?: return@forEachAll
            if (text.contains("说些什么")) {
                result = child
            }
        }
        return result
    }

    private fun findInputActions(root: View): View? {
        val group = root as? ViewGroup ?: return null
        for (i in 0 until group.childCount) {
            findInputActions(group.getChildAt(i))?.let { return it }
        }
        var hit = 0
        group.forEachAll { child ->
            val text = (child as? TextView)?.text?.toString() ?: return@forEachAll
            if (text == "粘贴" || text == "换行" || text == "发送") {
                hit++
            }
        }
        return if (hit >= 2) group else null
    }
}