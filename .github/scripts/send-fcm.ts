import admin from 'firebase-admin';
import { readFileSync } from 'fs';
import { join } from 'path';

interface AndroidNotificationConfig {
  title?: string;
  body?: string;
  icon?: string;
  color?: string;
  sound?: string;
  tag?: string;
  clickAction?: string;
  channelId?: string;
  ticker?: string;
  sticky?: boolean;
  eventTime?: string;
  localOnly?: boolean;
  notificationPriority?:
    | 'PRIORITY_MIN'
    | 'PRIORITY_LOW'
    | 'PRIORITY_DEFAULT'
    | 'PRIORITY_HIGH'
    | 'PRIORITY_MAX';
  defaultSound?: boolean;
  defaultVibrateTimings?: boolean;
  defaultLightSettings?: boolean;
  vibrateTimings?: string[];
  visibility?: 'PRIVATE' | 'PUBLIC' | 'SECRET';
  notificationCount?: number;
  lightSettings?: {
    color: { red: number; green: number; blue: number; alpha: number };
    lightOnDuration: string;
    lightOffDuration: string;
  };
  image?: string;
}

interface FCMMessage {
  notification?: {
    title: string;
    body: string;
    image?: string;
  };
  data?: Record<string, string>;
  android?: {
    priority?: 'normal' | 'high';
    ttl?: string;
    notification?: AndroidNotificationConfig;
  };
  apns?: {
    headers?: Record<string, string>;
    payload?: {
      aps: {
        badge?: number;
        sound?: string;
        alert?: { title: string; body: string };
        'thread-id'?: string;
        'content-available'?: number;
      };
    };
  };
  webpush?: {
    headers?: Record<string, string>;
    notification?: {
      title?: string;
      body?: string;
      icon?: string;
      badge?: string;
      tag?: string;
      renotify?: boolean;
      requireInteraction?: boolean;
      silent?: boolean;
    };
  };
  topic?: string;
  condition?: string;
  token?: string;
}

interface ScriptArgs {
  title: string;
  body: string;
  type?: string;
  image?: string;
  deep_link?: string;
  anime_id?: string;
  chapter_id?: string;
  id?: string;
  token?: string;
  topics?: string;
  condition?: string;
  android_channel_id?: string;
  android_priority?: string;
  android_ttl?: string;
  android_icon?: string;
  android_color?: string;
  android_visibility?: string;
  android_notification_count?: string;
  android_sound?: string;
  android_vibrate_timings?: string;
  android_light_settings?: string;
  android_default_light_settings?: string;
  android_default_vibrate_timings?: string;
  android_default_sound?: string;
  android_sticky?: string;
  android_local_only?: string;
  android_event_time?: string;
  android_tag?: string;
  android_click_action?: string;
  android_link?: string;
  android_group_key?: string;
  android_group_alert_behavior?: string;
  android_summary_text?: string;
  apns_priority?: string;
  apns_ttl?: string;
  apns_collapse_id?: string;
  apns_thread_id?: string;
  apns_badge?: string;
  apns_sound?: string;
  web_ttl?: string;
  web_urgency?: string;
  web_fallback_title?: string;
  web_fallback_body?: string;
  web_fallback_icon?: string;
  dry_run?: string;
}

function parseArgs(): ScriptArgs {
  const args: Record<string, string> = {};
  process.argv.slice(2).forEach((arg) => {
    if (arg.startsWith('--')) {
      const [key, ...valueParts] = arg.slice(2).split('=');
      args[key] = valueParts.join('=');
    }
  });
  return args as unknown as ScriptArgs;
}

function buildMessage(args: ScriptArgs): FCMMessage {
  const message: FCMMessage = {};

  message.notification = {
    title: args.title,
    body: args.body,
    ...(args.image && { image: args.image }),
  };

  message.data = {
    title: args.title,
    body: args.body,
    type: args.type || 'general',
    ...(args.image && { image: args.image }),
    ...(args.deep_link && { deep_link: args.deep_link }),
    ...(args.anime_id && { anime_id: args.anime_id }),
    ...(args.chapter_id && { chapter_id: args.chapter_id }),
    ...(args.id && { id: args.id }),
  };

  if (
    args.android_channel_id ||
    args.android_priority ||
    args.android_ttl ||
    args.android_icon ||
    args.android_color ||
    args.android_visibility ||
    args.android_notification_count ||
    args.android_sound ||
    args.android_vibrate_timings ||
    args.android_light_settings ||
    args.android_default_light_settings ||
    args.android_default_vibrate_timings ||
    args.android_default_sound ||
    args.android_sticky ||
    args.android_local_only ||
    args.android_event_time ||
    args.android_tag ||
    args.android_click_action ||
    args.android_link ||
    args.android_group_key ||
    args.android_group_alert_behavior ||
    args.android_summary_text
  ) {
    message.android = {};

    if (args.android_priority) {
      message.android.priority = args.android_priority as 'normal' | 'high';
    }
    if (args.android_ttl) {
      message.android.ttl = args.android_ttl;
    }

    const androidNotification: AndroidNotificationConfig = {};

    if (args.android_channel_id) androidNotification.channelId = args.android_channel_id;
    if (args.android_icon) androidNotification.icon = args.android_icon;
    if (args.android_color) androidNotification.color = args.android_color;
    if (args.android_sound) androidNotification.sound = args.android_sound;
    if (args.android_tag) androidNotification.tag = args.android_tag;
    if (args.android_click_action) androidNotification.clickAction = args.android_click_action;
    if (args.android_link) androidNotification.clickAction = args.android_link;
    if (args.android_visibility)
      androidNotification.visibility = args.android_visibility as 'PRIVATE' | 'PUBLIC' | 'SECRET';
    if (args.android_notification_count)
      androidNotification.notificationCount = parseInt(args.android_notification_count, 10);
    if (args.android_vibrate_timings) {
      try {
        androidNotification.vibrateTimings = JSON.parse(args.android_vibrate_timings);
      } catch {
        console.warn('Invalid vibrate_timings JSON, skipping');
      }
    }
    if (args.android_light_settings) {
      try {
        androidNotification.lightSettings = JSON.parse(args.android_light_settings);
      } catch {
        console.warn('Invalid light_settings JSON, skipping');
      }
    }
    if (args.android_default_sound)
      androidNotification.defaultSound = args.android_default_sound === 'true';
    if (args.android_default_vibrate_timings)
      androidNotification.defaultVibrateTimings = args.android_default_vibrate_timings === 'true';
    if (args.android_default_light_settings)
      androidNotification.defaultLightSettings = args.android_default_light_settings === 'true';
    if (args.android_sticky) androidNotification.sticky = args.android_sticky === 'true';
    if (args.android_local_only) androidNotification.localOnly = args.android_local_only === 'true';
    if (args.android_event_time) androidNotification.eventTime = args.android_event_time;
    if (args.android_group_key) androidNotification.clickAction = args.android_group_key;
    if (args.android_group_alert_behavior)
      androidNotification.clickAction = args.android_group_alert_behavior;
    if (args.android_summary_text) androidNotification.clickAction = args.android_summary_text;

    if (Object.keys(androidNotification).length > 0) {
      message.android!.notification = androidNotification;
    }
  }

  if (
    args.apns_priority ||
    args.apns_ttl ||
    args.apns_collapse_id ||
    args.apns_thread_id ||
    args.apns_badge ||
    args.apns_sound
  ) {
    message.apns = {
      headers: {},
      payload: { aps: {} },
    };

    if (args.apns_priority) {
      message.apns.headers!['apns-priority'] = args.apns_priority;
    }
    if (args.apns_ttl) {
      message.apns.headers!['apns-expiration'] = args.apns_ttl;
    }
    if (args.apns_collapse_id) {
      message.apns.headers!['apns-collapse-id'] = args.apns_collapse_id;
    }
    if (args.apns_thread_id) {
      message.apns.payload!.aps['thread-id'] = args.apns_thread_id;
    }
    if (args.apns_badge) {
      message.apns.payload!.aps.badge = parseInt(args.apns_badge, 10);
    }
    if (args.apns_sound) {
      message.apns.payload!.aps.sound = args.apns_sound;
    }
  }

  if (
    args.web_ttl ||
    args.web_urgency ||
    args.web_fallback_title ||
    args.web_fallback_body ||
    args.web_fallback_icon
  ) {
    message.webpush = {
      headers: {},
      notification: {},
    };

    if (args.web_ttl) {
      message.webpush.headers!['TTL'] = args.web_ttl;
    }
    if (args.web_urgency) {
      message.webpush.headers!['Urgency'] = args.web_urgency;
    }
    if (args.web_fallback_title) {
      message.webpush.notification!.title = args.web_fallback_title;
    }
    if (args.web_fallback_body) {
      message.webpush.notification!.body = args.web_fallback_body;
    }
    if (args.web_fallback_icon) {
      message.webpush.notification!.icon = args.web_fallback_icon;
    }
  }

  if (args.token) {
    message.token = args.token;
  } else if (args.condition) {
    message.condition = args.condition;
  } else if (args.topics) {
    const topicList = args.topics.split(',').map((t) => t.trim());
    if (topicList.length === 1) {
      message.topic = topicList[0];
    } else {
      message.condition = topicList.map((t) => `'${t}' in topics`).join(' || ');
    }
  } else {
    message.topic = 'all_users';
  }

  return message;
}

async function main() {
  const args = parseArgs();

  if (!args.title || !args.body) {
    console.error('Error: --title and --body are required');
    process.exit(1);
  }

  let serviceAccount;
  const envJson = process.env.FIREBASE_SERVICE_ACCOUNT;

  if (envJson) {
    try {
      serviceAccount = JSON.parse(envJson);
    } catch {
      console.error('Error: Invalid FIREBASE_SERVICE_ACCOUNT environment variable');
      process.exit(1);
    }
  } else {
    const filePath = join(process.cwd(), 'firebase-adminsdk.json');
    try {
      const fileContent = readFileSync(filePath, 'utf8');
      serviceAccount = JSON.parse(fileContent);
      console.log(`Loaded Firebase credentials from: ${filePath}`);
    } catch {
      console.error('Error: FIREBASE_SERVICE_ACCOUNT env not set and firebase-adminsdk.json not found');
      process.exit(1);
    }
  }

  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });

  const message = buildMessage(args);
  const dryRun = args.dry_run === 'true';

  console.log(`Sending notification: ${args.title}`);
  console.log(`Target: ${message.topic || message.condition}`);
  console.log(`Type: ${args.type || 'general'}`);
  console.log(`Dry run: ${dryRun}`);
  console.log(`Payload: ${JSON.stringify(message, null, 2)}`);

  try {
    const response = await admin.messaging().send(message, dryRun);
    console.log(`Success! Message ID: ${response}`);
    process.exit(0);
  } catch (error) {
    console.error('Failed to send notification:', error);
    process.exit(1);
  }
}

main();
