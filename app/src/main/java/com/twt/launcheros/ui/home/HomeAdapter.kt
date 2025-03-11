package com.twt.launcheros.ui.home

import android.content.pm.ApplicationInfo
import com.koai.base.main.adapter.BasePagingDataAdapter
import com.koai.base.main.extension.ClickableViewExtensions.loadImage
import com.koai.base.main.extension.safeClick
import com.twt.launcheros.R
import com.twt.launcheros.databinding.ItemAppBinding

class HomeAdapter : BasePagingDataAdapter<ApplicationInfo, ItemAppBinding>(){
    override fun bindView(holder: VH, binding: ItemAppBinding, position: Int) {
        val item = getItem(position)?:return
        binding.icon.loadImage(item.loadIcon(binding.root.context.packageManager))
        binding.name.text = item.loadLabel(binding.root.context.packageManager)
        binding.root.safeClick(50) {
            try {
                val intent = binding.root.context.packageManager.getLaunchIntentForPackage(item.packageName)
                binding.root.context.startActivity(intent)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        binding.executePendingBindings()
    }

    override fun getLayoutId(viewType: Int) = R.layout.item_app
}