package xyz.hisname.fireflyiii.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_dashboard_recent_transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import xyz.hisname.fireflyiii.R
import xyz.hisname.fireflyiii.repository.models.transaction.TransactionData
import xyz.hisname.fireflyiii.repository.viewmodel.TransactionViewModel
import xyz.hisname.fireflyiii.ui.base.BaseFragment
import xyz.hisname.fireflyiii.util.extension.create
import xyz.hisname.fireflyiii.util.extension.getViewModel
import java.util.*

class RecentTransactionFragment: BaseFragment() {

    private val model: TransactionViewModel by lazy { getViewModel(TransactionViewModel::class.java) }
    private val transactionList by lazy { model.getTransactions(baseUrl, accessToken,
            null, null, "Withdrawal").databaseData }

    private var dataAdapter = ArrayList<TransactionData>()
    private lateinit var rtAdapter: TransactionRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.create(R.layout.fragment_dashboard_recent_transaction, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTransaction()
    }

    private fun loadTransaction() {
        dataAdapter.clear()
        transactionLoader.bringToFront()
        transactionLoader.show()
        recentTransactionList.layoutManager = LinearLayoutManager(requireContext())
        recentTransactionList.addItemDecoration(DividerItemDecoration(recentTransactionList.context,
                DividerItemDecoration.VERTICAL))
        launch(context = Dispatchers.Main) {
            async(Dispatchers.IO) {
                transactionList
            }.await()
            transactionLoader.hide()
            dataAdapter = ArrayList(transactionList)
            if (dataAdapter.size == 0) {
                recentTransactionList.isGone = true
                noTransactionText.isVisible = true
            } else {
                recentTransactionList.isVisible = true
                noTransactionText.isGone = true
                if (dataAdapter.size <= 5) {
                    rtAdapter = TransactionRecyclerAdapter(dataAdapter, "recent")
                } else {
                    // More than 5 index in json so we get first 5 only
                    dataAdapter.subList(5, dataAdapter.size).clear()
                    rtAdapter = TransactionRecyclerAdapter(dataAdapter, "recent")
                }
                recentTransactionList.adapter = rtAdapter
                rtAdapter.apply {
                    recentTransactionList.adapter as TransactionRecyclerAdapter
                }
                rtAdapter.notifyDataSetChanged()

            }
        }
    }

}