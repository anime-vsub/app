package git.shin.animevsub;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;

import java.util.ArrayList;

public class MainActivity extends BridgeActivity {
  // no code
  @Override
  public void onStart() {
    super.onStart();
    bridge.getWebView().setVerticalScrollBarEnabled(false);
  }
}
