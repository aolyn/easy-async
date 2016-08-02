// package org.aolyn.concurrent;
//
// import org.aolyn.concurrent.context.CallContext;
//
// import java.io.Closeable;
// import java.io.IOException;
//
// public class ContinueWithSpanNameSetter implements Closeable {
//     public ContinueWithSpanNameSetter(String name) {
//         CallContext.setData("continueWithSpanName", name);
//     }
//
//     @Override
//     public void close() throws IOException {
//         CallContext.setData("continueWithSpanName", null);
//     }
// }
