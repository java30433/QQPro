package momoi.mod.qqpro.hook.action

import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.tencent.qqnt.chats.core.adapter.itemdata.RecentContactChatItem
import com.tencent.qqnt.kernel.nativeinterface.RecentContactInfo
import com.tencent.qqnt.watch.chat.list.WatchRecentContactHolder
import com.tencent.qqnt.watch.chat.list.WatchRecentItemBuilder
import com.tencent.widget.SingleLineTextView
import momoi.anno.mixin.Mixin
import momoi.mod.qqpro.util.Utils

object RecentContacts {
    private val timePattern = Regex("\\s*\\d{1,2}:\\d{2}$")
    val map = mutableMapOf<String, Data>()
    fun get(peerUin: String?) = map[peerUin]
    class Data(
        val raw: RecentContactInfo,
        val unreadCntCached: Int,
    ) {
        val atType get() = raw.atType
    }

    fun compactHomeText(holder: WatchRecentContactHolder) {
        val time = holder.b.d.text?.toString()?.trim().orEmpty()
        holder.b.c.compactTitle(14f, 8, time)
        holder.b.d.visibility = View.GONE
        holder.b.e.compactSingleLine(13f, 12)
    }

    private fun SingleLineTextView.compactTitle(size: Float, maxChars: Int, time: String) {
        val title = text.toString().replace(timePattern, "").shortText(maxChars)
        text = if (time.isNotEmpty()) "$title $time" else title
        setTextSize(size)
        setMaxWidth((resources.displayMetrics.widthPixels * 0.72f).toInt())
        m = TextUtils.TruncateAt.END
        requestLayout()
    }

    private fun SingleLineTextView.compactSingleLine(size: Float, maxChars: Int) {
        text = text.shortText(maxChars)
        setTextSize(size)
        setMaxWidth((resources.displayMetrics.widthPixels * 0.5f).toInt())
        m = TextUtils.TruncateAt.END
        requestLayout()
    }

    private fun TextView.compactSingleLine(size: Float, maxChars: Int) {
        text = text.shortText(maxChars)
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        includeFontPadding = false
        setSingleLine(true)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    private fun CharSequence?.shortText(maxChars: Int): CharSequence {
        val raw = this?.toString()?.trim().orEmpty()
        return if (raw.length > maxChars) raw.take(maxChars) + "..." else raw
    }

    @Mixin
    abstract class Hook : WatchRecentItemBuilder() {
        override fun t(item: RecentContactChatItem, holder: WatchRecentContactHolder) {
            Utils.log("load recent contact: ${item.a.peerName}, unreadCnt: ${item.a.unreadCnt}, chatCnt: ${item.a.unreadChatCnt}, peerUid: ${item.a.peerUid}")
            map[item.a.peerUid] = Data(
                item.a,
                item.a.unreadCnt.toInt()
            )
            super.t(item, holder)
            compactHomeText(holder)
        }
    }
}