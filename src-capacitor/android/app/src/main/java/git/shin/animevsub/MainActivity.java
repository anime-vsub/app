package git.shin.animevsub;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;

import java.util.ArrayList;

public class MainActivity extends BridgeActivity {
  // no code
//  @Override
//  public void onCreate() {
//    registerPlugin(ResolvePlugin.class);
//    super.onStart();
//    bridge.getWebView().setVerticalScrollBarEnabled(false);
//  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    registerPlugin(ResolvePlugin.class);
    super.onCreate(savedInstanceState);
    bridge.getWebView().setVerticalScrollBarEnabled(false);
  }
}
