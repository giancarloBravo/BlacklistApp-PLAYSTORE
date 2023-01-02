package pe.com.gianbravo.blockedcontacts.presentation

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.BlockedNumberContract
import android.view.*
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import pe.com.gianbravo.blockedcontacts.presentation.view.touchHelper.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_blocked_numbers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.gianbravo.blockedcontacts.R
import pe.com.gianbravo.blockedcontacts.domain.BlacklistContacts
import pe.com.gianbravo.blockedcontacts.presentation.adapter.RvNumberListAdapter
import pe.com.gianbravo.blockedcontacts.presentation.base.BaseFragment
import pe.com.gianbravo.blockedcontacts.presentation.dialog.HowToUseDialogFragment
import pe.com.gianbravo.blockedcontacts.toast
import pe.com.gianbravo.blockedcontacts.utils.Constants.FILE_NAME
import pe.com.gianbravo.blockedcontacts.utils.DialogUtil
import java.io.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Giancarlo Bravo Anlas
 *
 */
class BlockedNumbersFragment : BaseFragment(), CoroutineScope{

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    private lateinit var rvAdapter: RvNumberListAdapter
    private lateinit var callback: SimpleItemTouchHelperCallback

    private val fileType = "application/json"

    private val openRegisterForResult =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                lateinit var stringBuilder: StringBuilder
                var fileFound = false
                showFullScreenLoader()
                launch {
                    //read file
                    try {
                        val fileInputStream = BufferedInputStream(
                            requireContext().contentResolver.openInputStream(uri)
                        )
                        val inputStreamReader = InputStreamReader(fileInputStream)
                        val bufferedReader = BufferedReader(inputStreamReader)
                        stringBuilder = StringBuilder()
                        var text: String? = null
                        while ({ text = bufferedReader.readLine(); text }() != null) {
                            stringBuilder.append(text)
                        }
                        fileInputStream.close()
                        fileFound = true
                    } catch (e: Exception) {
                        // Notify User of fail
                    }

                    // parse to object
                    val blacklistContacts: BlacklistContacts =
                        Gson().fromJson(stringBuilder.toString(), BlacklistContacts::class.java)

                    // load numbers to blacklist
                    blacklistContacts.list.forEach {
                        putNumberOnBlocked(it, true)
                    }

                    withContext(Dispatchers.Main) {
                        if (fileFound) {
                            context?.toast(getString(R.string.text_added_numbers))
                            // load to adapter
                            refreshList()
                        } else
                            context?.toast(getString(R.string.error_read_file))
                        dismissFullScreenLoader()
                    }
                }
            } else {
                context?.toast(getString(R.string.error_file))
            }
        }

    private val createRegisterForResult =
        registerForActivityResult(ActivityResultContracts.CreateDocument(fileType)) { uri ->
            if (uri != null) {
                showFullScreenLoader()
                // Get the data
                lateinit var outputJson: String
                val list = rvAdapter.getAnswers()
                val data = BlacklistContacts(
                    list.size,
                    list
                )
                val gson = Gson()
                outputJson = gson.toJson(data)

                launch {
                    // Save the data into the selected file
                    writeFile(requireContext(), outputJson, uri)

                    withContext(Dispatchers.Main) {
                        context?.toast(getString(R.string.text_export_success))
                        dismissFullScreenLoader()

                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        val intentShareFile = Intent(Intent.ACTION_SEND)

                        // share via email
                            intentShareFile.type = fileType
                            intentShareFile.putExtra(
                                Intent.EXTRA_STREAM,
                               uri
                            )
                            intentShareFile.putExtra(
                                Intent.EXTRA_SUBJECT,
                                getString(R.string.text_share_subject)
                            )
                            intentShareFile.putExtra(
                                Intent.EXTRA_TEXT,
                                getString(R.string.text_share_text)
                            )
                            startActivity(Intent.createChooser(intentShareFile, "Share File"))
                    }
                }
            } else
                context?.toast(getString(R.string.error_file))
            dismissFullScreenLoader()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blocked_numbers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObservers()
        registerListeners()
        setupViews()
    }

    private fun registerObservers() {}

    private fun registerListeners() {
        buttonBlock.setOnClickListener {
            val number = etNumber.text.toString()
            putNumberOnBlocked(number)
        }

        buttonRemove.setOnClickListener {
            val number = etNumber.text.toString()
            removeNumberFromBlocker(number)
            refreshList()
        }

        buttonExport.setOnClickListener {
            exportBlacklist()
        }
        buttonImport.setOnClickListener {
            importBlacklist()
        }
    }

    private fun setupViews() {
        setupMenu()

        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = getString(R.string.app_name)

        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        rvList.layoutManager = linearLayoutManager
        rvAdapter =
            RvNumberListAdapter(
                context,
                null,
                object :
                    RvNumberListAdapter.OnItemSelected {
                    override fun onItemSelected(
                        item: String?,
                        buttonView: CompoundButton?,
                        isChecked: Boolean
                    ) {
                        etNumber.setText(item)
                    }
                },
                object :
                    RvNumberListAdapter.OnItemRemove {
                    override fun onItemRemove(item: String?, position: Int) {

                        DialogUtil.showDialogListener(context,
                            getString(R.string.text_remove_1) + " '$item' " + getString(R.string.text_remove_2),
                            true,
                            closeEnabled = true,
                            sendEnabled = true,
                            onEventDialog = object : DialogUtil.OnEventDialog {
                                override fun onClickSend() {
                                    removeNumberFromBlocker(item)
                                    rvAdapter.removeItem(position)
                                }

                                override fun onCancel() {
                                    rvAdapter.notifyItemChanged(position)
                                }

                            })

                    }
                })
        rvList.adapter = rvAdapter

        callback = SimpleItemTouchHelperCallback(rvAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(rvList)

        refreshList()
    }

    private fun setupMenu() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_blacklist, menu)
                menu.findItem(R.id.action_info).apply{
                    isVisible = true
                }
                menu.findItem(R.id.action_caller).apply{
                    isVisible = true
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_info -> {
                        showHowToDialog()
                        true
                    }
                    R.id.action_caller -> {
                        showChangeDefaultDialerSelector()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun putNumberOnBlocked(
        number: String,
        isFromMultiple: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val values = ContentValues()
            values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
            context?.contentResolver?.insert(
                BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                values
            )
            if (!isFromMultiple) {
                context?.toast(getString(R.string.text_added_number))
                etNumber.setText("")
                refreshList()
            }
        } else
            context?.toast(getString(R.string.text_not_supported))
    }

    private fun refreshList() {
        rvAdapter.loadData(getBlocked())
    }

    private fun getBlocked(): ArrayList<String> {
        val list = arrayListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.contentResolver?.let {
                val record: Cursor? =
                    it.query(
                        BlockedNumberContract.BlockedNumbers.CONTENT_URI, arrayOf(
                            BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                            BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                            BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
                        ), null, null, null
                    )

                if (record != null && record.count != 0) {
                    if (record.moveToFirst()) {
                        do {
                            val blockNumber =
                                record.getString(record.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER))
                            list.add(blockNumber)
                        } while (record.moveToNext())
                    }
                    record.close()
                }
            }
        }
        list.sort()
        return list
    }

    private fun removeNumberFromBlocker(number: String?) {
        number?.let {
            val values = ContentValues()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
                val uri =
                    context?.contentResolver?.insert(
                        BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                        values
                    )
                uri?.let { it1 -> context?.contentResolver?.delete(it1, null, null) }
                context?.toast(getString(R.string.text_removed_number))
                etNumber.setText("")
            }
        }
    }

    private fun exportBlacklist() {
        showFullScreenLoader()
        this.createRegisterForResult.launch(FILE_NAME)
    }

    private fun importBlacklist() {
        this.openRegisterForResult.launch(arrayOf(fileType))
    }

    private fun writeFile(context: Context, outputJson: String, uri: Uri): Boolean {
        var bos: BufferedOutputStream? = null
        // Now read the file
        try {
            bos = BufferedOutputStream(context.contentResolver.openOutputStream(uri))
            bos.write(outputJson.toByteArray())
            bos.close()

        } catch (e: Exception) {
            // Notify User of fail
            context.toast(getString(R.string.text_removed_number))
            return false
        } finally {
            try {
                if (bos != null) {
                    bos.flush()
                    bos.close()
                }
            } catch (ignored: Exception) {
            }
        }
        return true
    }

    private fun showHowToDialog() {
        val dialogModifyEntriesFragment =
            HowToUseDialogFragment()
        val bundle = Bundle()
        dialogModifyEntriesFragment.arguments = bundle
        childFragmentManager.let {
            dialogModifyEntriesFragment.show(it, "aea")
        }
    }

    private fun showChangeDefaultDialerSelector() {
        if (activity is DialerActivity)
            (activity as DialerActivity).showChangeDefaultDialerSelector(true)
    }
}
