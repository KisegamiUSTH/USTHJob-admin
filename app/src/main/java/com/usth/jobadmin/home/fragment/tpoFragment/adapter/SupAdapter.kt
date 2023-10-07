package com.usth.jobadmin.home.fragment.supFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.usth.jobadmin.databinding.SupCardLayoutBinding
import com.usth.jobadmin.home.fragment.supFragment.SupFragment
import com.usth.jobadmin.model.Sup

class SupAdapter(
    private val listener: SupFragment
) : RecyclerView.Adapter<SupAdapter.SupViewHolder>() {

    private val supList: MutableList<Sup> = mutableListOf()

    inner class SupViewHolder(
        private val binding: SupCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sup: Sup) {
            binding.tvSupName.text = sup.username
            binding.tvSupEmail.text = sup.email
            binding.ivSupProfile.load(sup.imageUri)
            binding.cvPlacementOfficer.setOnClickListener {
                listener.navigateToSupView(sup)
            }
            binding.ivDeleteSup.setOnClickListener {
                listener.deleteSup(sup)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupViewHolder {
        val view = SupCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupViewHolder(view)
    }

    override fun onBindViewHolder(holder: SupViewHolder, position: Int) {
        holder.bind(supList[position])
    }

    override fun getItemCount(): Int = supList.size

    fun setData(newSup: List<Sup>) {
        supList.clear()
        supList.addAll(newSup)
        notifyDataSetChanged()
    }
}