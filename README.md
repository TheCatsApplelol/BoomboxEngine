# BoomboxEngine
A custom, thread-safe, polyphonic Java 8 sound engine. Built to be lightweight, lightning-fast, and super easy to use in your Java games or desktop apps! 


[![Java 8](https://img.shields.io/badge/Java-8-orange.svg)](https://www.java.com/)

[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

# API Reference

```engine.load(String name, String filepath)```

Reads a .wav file and caches it in memory.

name: A nickname for your sound (e.g., "laser").

filepath: Where the file is located (e.g., "assets/laser.wav").

```engine.loadResource(String name, String resourcePath)```

Same as load, but pulls the .wav straight off your classpath instead of the disk.

name: A nickname for your sound (e.g., "laser").

resourcePath: Where the file lives on the classpath (e.g., "/sounds/laser.wav").

Heads up: paths are resolved relative to BoomboxEngine's package unless they start with a `/`. So a file sitting at the root of your jar is "/laser.wav", not "laser.wav". If you ever get a "Resource not found", a missing leading slash is almost always the culprit.


```engine.play(String name)```

Plays a loaded sound at maximum volume (1.0f). Spawns a background thread automatically.

```engine.play(String name, float volume)```

Plays a loaded sound at a specific volume.

volume: A float between 0.0f (mute) and 1.0f (max).

```engine.shutdown()```

Clears the RAM cache and safely closes the background thread pool. Always call this when exiting your app to prevent background tasks from hanging!

# Installation

Literally just download BoomboxEngine.java and put it in your source folder next to your other Java code. 

To start up the engine, simply create a new instance of it in your code like this:

```BoomboxEngine engine = new BoomboxEngine();```

# Why should i use this?

You should not!
