package git.shin.animevsub

import android.os.Bundle
import com.getcapacitor.BridgeActivity

class MainActivity : BridgeActivity() {
    // no code
    //  @Override
    //  public void onCreate() {
    //    registerPlugin(ResolvePlugin.class);
    //    super.onStart();
    //    bridge.getWebView().setVerticalScrollBarEnabled(false);
    //  }
    public override fun onCreate(savedInstanceState: Bundle?) {
        registerPlugin(ResolvePlugin::class.java)
        super.onCreate(savedInstanceState)
        bridge.webView.isVerticalScrollBarEnabled = false
    }
}
