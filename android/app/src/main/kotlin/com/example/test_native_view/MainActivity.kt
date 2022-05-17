package com.example.test_native_view

import NativeViewFactory
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        flutterEngine
            .platformViewsController
            .registry
            .registerViewFactory(
                "video_player",
                NativeViewFactory(flutterEngine.dartExecutor.binaryMessenger,this)
            )
        GeneratedPluginRegistrant.registerWith(flutterEngine);
//         NativeVIewHandler.registerWith(flutterEngine.dartExecutor.binaryMessenger)
//        flutterEngine.getPlugins().add(ConnectivityPlugin());
//        flutterEngine.getPlugins().add( AppSettingsPlugin())
//        flutterEngine.getPlugins().add(SqflitePlugin())
    }
}
