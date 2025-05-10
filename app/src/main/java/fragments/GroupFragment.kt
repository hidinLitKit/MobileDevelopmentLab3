package fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.listlab.MainActivity
import com.example.listlab.R
import com.example.listlab.databinding.FragmentGroupBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import data.Group
import interfaces.MainActivityCallbacks
import view_models.GroupViewModel

class GroupFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE: GroupFragment? = null
        fun getInstance(): GroupFragment {
            if (INSTANCE == null) INSTANCE = GroupFragment()
            return INSTANCE ?: throw Exception("GroupFragment не создан")
        }

        fun newInstance(): GroupFragment {
            INSTANCE = GroupFragment()
            return INSTANCE!!
        }
    }

    private lateinit var viewModel: GroupViewModel
    private var tabPosition: Int = 0
    private lateinit var _binding: FragmentGroupBinding
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[GroupViewModel::class.java]
        val ma = (requireActivity() as MainActivityCallbacks)
        ma.newTitle("Факультет \"${viewModel.faculty?.name}\"")

        viewModel.groupList.observe(viewLifecycleOwner) {

            createUI(it.filter{it.facultyID == viewModel.faculty!!.id})
        }
    }

    override fun append() {
        editGroup()
    }

    override fun update() {
        editGroup(viewModel?.group?.name ?: "")
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog()
    {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить группу ${viewModel.faculty?.name ?: "" }?")
            .setPositiveButton("Да"){ _, _ ->
                viewModel.deleteGroup()
            }
            .setNegativeButton("Нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editGroup(groupName: String = "") {
        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo1)
        val inputString = mDialogView.findViewById<EditText>(R.id.editString)
        inputString.setText(groupName)
        messageText.text = "Укажите наименование группы"



        AlertDialog.Builder(requireContext())
            .setTitle("Изменение данных")
            .setView(mDialogView)
            .setPositiveButton("подтверждаю") { _, _ ->
                if (inputString.text.isNotBlank()) {
                    if (groupName.isBlank())
                        viewModel.appendGroup(inputString.text.toString())
                    else
                        viewModel.updateGroup(inputString.text.toString())
                }
            }
            .setNegativeButton("отмена", null)
            .setCancelable(true)
            .create()
            .show()

    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val groups: List<Group>?) :
        FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            return (groups?.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment {
            return StudentsListFragment.newInstance(groups!![position])
        }

    }

    fun createUI(groupList: List<Group>) {
        binding.tbGroups.clearOnTabSelectedListeners()
        binding.tbGroups.removeAllTabs()

        for (i in 0 until (groupList.size)) {
            binding.tbGroups.addTab(binding.tbGroups.newTab().apply {
                text = groupList.get(i).name
            })
        }

        val adapter = GroupPageAdapter(requireActivity(), groupList)
        binding.vpGroupInfo.adapter = adapter

        TabLayoutMediator(binding.tbGroups, binding.vpGroupInfo, true, true) { tab, pos ->
            tab.text = groupList.get(pos).name
        }.attach()

        tabPosition = 0
        if (viewModel.group != null) {
            tabPosition = if (viewModel.getGroupListPosition >= 0)
                viewModel.getGroupListPosition
            else
                0
        }

        viewModel.setCurrentGroup(tabPosition)
        binding.tbGroups.selectTab(binding.tbGroups.getTabAt(tabPosition), true)

        binding.tbGroups.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
                viewModel.setCurrentGroup(groupList[tabPosition])
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })




    }
}