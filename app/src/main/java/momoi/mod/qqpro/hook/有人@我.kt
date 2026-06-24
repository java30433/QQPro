package momoi.mod.qqpro.hook

import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.tencent.qqnt.chats.core.adapter.holder.BaseChatViewHolder
import com.tencent.qqnt.chats.core.adapter.itemdata.BaseChatItem
import com.tencent.qqnt.chats.core.adapter.itemdata.RecentContactChatItem
import com.tencent.qqnt.watch.chat.list.WatchRecentContactHolder
import com.tencent.qqnt.watch.chat.list.WatchRecentItemBuilder
import momoi.anno.mixin.Mixin
import momoi.mod.qqpro.Colors
import momoi.mod.qqpro.hook.action.RecentContacts
import momoi.mod.qqpro.lib.clickable

const val TOKEN = "\u200B\u200B\u200B\u200B\u200B"
val atList = mutableListOf<String>()

@Mixin
abstract class 有人at我 : WatchRecentItemBuilder() {
    override fun m(
        p0: BaseChatViewHolder<BaseChatItem?>,
        p1: RecentContactChatItem,
        p2: List<Any?>
    ) {
        super.m(p0, p1, p2)
        val tv = (p0 as WatchRecentContactHolder).b.c
        p0.itemView.clickable {
            m(p0, p1, p2)
            tv.text = tv.text.toString().removeBefore(TOKEN)
        }
        if (p1.a.notifiedType != 0 && p1.a.atType == 6) {
            atList.add(p1.a.peerUid)
        }
        if (p1.a.unreadCnt == 0L) {
            atList.remove(p1.a.peerUid)
            tv.text = tv.text.toString().removeBefore(TOKEN)
        }
        if (atList.contains(p1.a.peerUid)) {
            tv.text = buildSpannedString {
                color(Colors.atMe) {
                    append("[有人@我]")
                }
                append(TOKEN)
                append(tv.text.toString().removeBefore(TOKEN))
            }
        }
        RecentContacts.compactHomeText(p0)
    }
}

private fun String.removeBefore(token: String): String {
    val index = indexOf(token)
    return if (index != -1) substring(index + token.length) else this
}