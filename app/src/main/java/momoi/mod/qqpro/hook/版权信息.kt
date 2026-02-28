package momoi.mod.qqpro.hook

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tencent.qqnt.watch.selftab.ui.SelfFragment
import momoi.anno.mixin.Mixin

const val VERSION_CODE = 12

@Mixin
class 版权信息 : SelfFragment() {
    @SuppressLint("ResourceType", "SetTextI18n")
    override fun Y(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val result = super.Y(inflater, container, savedInstanceState)
        val tv = result.findViewById<TextView>(2114521808)
        tv.text = buildString {
            appendLine("QQPro - v1.5.2")
            appendLine()
            appendLine("更新日志：")
            appendLine("优化回复消息溯源")
            appendLine("更换等级头衔相关内容接口")
            appendLine("调整管理撤回按钮位置")
            appendLine("调整等级头衔位置")
            appendLine("也许修复卡片消息图片尺寸问题")
            appendLine("[有人@我]但是bug有点多，就这样吧")
			appendLine("添加无障碍保活服务")
            appendLine()
            appendLine("交流群：392106734")
            appendLine("2025/02/28")
        }
        return result
    }
}