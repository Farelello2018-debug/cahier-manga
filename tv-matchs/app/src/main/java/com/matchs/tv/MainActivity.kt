package com.matchs.tv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var urlInput: EditText
    private lateinit var searchInput: EditText
    private lateinit var status: TextView
    private val adapter = ChannelAdapter { index -> openPlayer(index) }
    private var allChannels: List<Channel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.urlInput)
        searchInput = findViewById(R.id.searchInput)
        status = findViewById(R.id.status)

        val list = findViewById<RecyclerView>(R.id.channelList)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        val prefs = getSharedPreferences("matchs", Context.MODE_PRIVATE)
        urlInput.setText(prefs.getString("url", ""))

        findViewById<Button>(R.id.loadButton).setOnClickListener { load(urlInput.text.toString()) }
        urlInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) { load(urlInput.text.toString()); true } else false
        }
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) = applyFilter()
        })

        val presetRow = findViewById<LinearLayout>(R.id.presets)
        for ((label, url) in Presets.AFRICA) {
            val b = Button(this).apply {
                text = label
                isAllCaps = false
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.text))
                setBackgroundResource(R.drawable.focus_bg)
                setPadding(32, 0, 32, 0)
                setOnClickListener {
                    urlInput.setText(url)
                    load(url)
                }
            }
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginEnd = 16 }
            presetRow.addView(b, lp)
        }

        val saved = prefs.getString("url", "").orEmpty()
        if (saved.isNotBlank()) load(saved)
    }

    private fun load(rawUrl: String) {
        val url = rawUrl.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            status.text = "L'adresse doit commencer par http:// ou https://"
            return
        }
        getSharedPreferences("matchs", Context.MODE_PRIVATE).edit().putString("url", url).apply()
        status.text = "Chargement de la liste…"
        thread {
            val outcome = runCatching { M3uParser.parse(M3uParser.download(url)) }
            runOnUiThread {
                outcome.onSuccess { channels ->
                    allChannels = channels
                    searchInput.setText("")
                    applyFilter()
                }.onFailure { e ->
                    allChannels = emptyList()
                    applyFilter()
                    status.text = "Impossible de charger la liste : ${e.message ?: e.javaClass.simpleName}"
                }
            }
        }
    }

    private fun applyFilter() {
        val q = searchInput.text.toString().trim()
        val shown = if (q.isEmpty()) allChannels else allChannels.filter {
            it.name.contains(q, ignoreCase = true) || it.group.contains(q, ignoreCase = true)
        }
        ChannelStore.channels = shown
        adapter.submit(shown)
        status.text = when {
            allChannels.isEmpty() -> getString(R.string.empty)
            shown.isEmpty() -> "Aucune chaîne ne correspond à « $q »."
            else -> "${shown.size} chaîne(s). Choisis-en une avec la télécommande, puis OK."
        }
    }

    private fun openPlayer(index: Int) {
        startActivity(Intent(this, PlayerActivity::class.java).putExtra(PlayerActivity.EXTRA_INDEX, index))
    }
}

class ChannelAdapter(private val onPick: (Int) -> Unit) : RecyclerView.Adapter<ChannelAdapter.Holder>() {

    private var items: List<Channel> = emptyList()

    fun submit(list: List<Channel>) {
        items = list
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val group: TextView = view.findViewById(R.id.group)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val c = items[position]
        holder.name.text = c.name
        holder.group.text = c.group
        holder.group.visibility = if (c.group.isEmpty()) View.GONE else View.VISIBLE
        holder.itemView.setOnClickListener { onPick(holder.bindingAdapterPosition) }
    }
}
