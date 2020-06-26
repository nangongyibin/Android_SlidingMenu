# 
侧滑菜单


## How to ##

To get a Git project into your build:

### Step 1. Add the JitPack repository to your build file ###

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


### Step 2. Add the dependency ###

	dependencies {
	        implementation 'com.github.nangongyibin:Android_SlidingMenu:1.0.3'
	}

### update logs ###

①slidingmenu:menu's layout at before,main's at after.