package git.shin.animevsub;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CapacitorPlugin(name = "Resolve")
public class ResolvePlugin extends Plugin {

  @PluginMethod()
  public void echo(PluginCall call) {
    String value = call.getString("value");

    JSObject ret = new JSObject();
    ret.put("value", value);
    call.resolve(ret);
  }

  @PluginMethod()
  public void resolve(PluginCall call) {
    String url = call.getString("url");
    JSObject headers = call.getObject("headers");

    if (url == null) {
      call.reject("Must provide a URL");
      return;
    }

    JSObject ret = resolveUrl(url, headers);
    if (ret.has("error")) {
      call.reject(ret.getString("error"));
    } else {
      call.resolve(ret);
    }
  }
  @PluginMethod()
  public void resolveAll(PluginCall call) {
    JSArray urlArray = call.getArray("urls");
//    JSArray headersArray = call.getArray("headers") ?? [];

    if (urlArray == null) {
      call.reject("Must provide an array of URLs");
      return;
    }

    try {
      List<CompletableFuture<JSObject>> futures = new ArrayList<>();
      List<JSONObject> urls = urlArray.toList();

      List<JSObject> results = new ArrayList<>();

      for (JSONObject jsonObject : urls) {
        JSObject urlObject = JSObject.fromJSONObject(jsonObject);

        String url = urlObject.getString("url");
        JSObject headers = urlObject.getJSObject("headers");

        results.add(resolveUrl(url, headers));
      }

      call.resolve(new JSObject().put("results", new JSArray(results)));
    } catch (JSONException e) {
      call.reject("Error processing URLs array: " + e.getMessage());
    }
  }

  private JSObject resolveUrl(String url, JSObject headers) {
    JSObject result = new JSObject();

    try {
      URL obj = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

      // Set headers
      if (headers != null) {
        Iterator<String> keys = headers.keys();
        while (keys.hasNext()) {
          String key = keys.next();
          connection.setRequestProperty(key, headers.getString(key));
        }
      }

      connection.setInstanceFollowRedirects(false);
      connection.connect();

      String locationHeader = connection.getHeaderField("Location");
      String resolvedUrl = (locationHeader != null) ? locationHeader : url;

      result.put("url", resolvedUrl);
    } catch (IOException e) {
      result.put("error", e.getMessage());
    }

    return result;
  }
}
