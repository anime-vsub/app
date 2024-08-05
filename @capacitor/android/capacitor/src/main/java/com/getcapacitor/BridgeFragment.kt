package com.getcapacitor

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.getcapacitor.android.R

/**
 * A simple [Fragment] subclass.
 * Use the [BridgeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BridgeFragment : Fragment() {
    var bridge: Bridge? = null
        protected set
    protected var keepRunning: Boolean = true

    private val initialPlugins: MutableList<Class<out Plugin>> = ArrayList()
    private var config: CapConfig? = null

    private val webViewListeners: MutableList<WebViewListener> = ArrayList()

    fun addPlugin(plugin: Class<out Plugin>) {
        initialPlugins.add(plugin)
    }

    fun setConfig(config: CapConfig?) {
        this.config = config
    }

    fun addWebViewListener(webViewListener: WebViewListener) {
        webViewListeners.add(webViewListener)
    }

    /**
     * Load the WebView and create the Bridge
     */
    protected fun load(savedInstanceState: Bundle?) {
        Logger.Companion.debug("Loading Bridge with BridgeFragment")

        val args = arguments
        var startDir: String? = null

        if (args != null) {
            startDir = arguments!!.getString(ARG_START_DIR)
        }

        bridge =
            Bridge.Builder(this)
                .setInstanceState(savedInstanceState)
                .setPlugins(initialPlugins)
                .setConfig(config)
                .addWebViewListeners(webViewListeners)
                .create()

        if (startDir != null) {
            bridge!!.setServerAssetPath(startDir)
        }

        this.keepRunning = bridge!!.shouldKeepRunning()
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)

        val a = context.obtainStyledAttributes(attrs, R.styleable.bridge_fragment)
        val c: CharSequence? = a.getString(R.styleable.bridge_fragment_start_dir)

        if (c != null) {
            val startDir = c.toString()
            val args = Bundle()
            args.putString(ARG_START_DIR, startDir)
            arguments = args
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bridge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.load(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.bridge != null) {
            bridge!!.onDestroy()
        }
    }

    companion object {
        private const val ARG_START_DIR = "startDir"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param startDir the directory to serve content from
         * @return A new instance of fragment BridgeFragment.
         */
        fun newInstance(startDir: String?): BridgeFragment {
            val fragment = BridgeFragment()
            val args = Bundle()
            args.putString(ARG_START_DIR, startDir)
            fragment.arguments = args
            return fragment
        }
    }
}
