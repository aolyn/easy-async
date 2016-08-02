// package org.aolyn.concurrent;
//
// import org.aolyn.concurrent.context.CallContext;
//
// import java.io.Closeable;
// import java.io.IOException;
//
// public class ContinueWithSpanSwitchSetter implements Closeable {
//     private static final String key = "TaskUtil.continueWith_logSpan";
//
//     public ContinueWithSpanSwitchSetter(boolean logSpan) {
//         CallContext.setData(key, logSpan);
//     }
//
//     @Override
//     public void close() throws IOException {
//         CallContext.setData(key, null);
//     }
// }
