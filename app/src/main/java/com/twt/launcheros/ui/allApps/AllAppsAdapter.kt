package com.twt.launcheros.ui.allApps

import com.koai.base.main.adapter.BasePagingDataAdapter
import com.koai.base.main.extension.ClickableViewExtensions.loadImage
import com.koai.base.main.extension.safeClick
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ItemAppBinding
import com.twt.launcheros.model.AppModel

class AllAppsAdapter(private val onClick: (item: AppModel) -> Unit = { _ -> }) :
    BasePagingDataAdapter<AppModel, ItemAppBinding>() {
    override fun bindView(
        holder: VH,
        binding: ItemAppBinding,
        position: Int,
    ) {
        val item = getItem(position) ?: return
        binding.icon.loadImage(item.icon)
        binding.name.text = item.label
        binding.root.safeClick(50) {
            onClick.invoke(item)
        }
        binding.executePendingBindings()
    }

    override fun getLayoutId(viewType: Int) = R.layout.item_app
}
