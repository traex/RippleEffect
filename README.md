RippleEffect
================

![RippleEffect](https://github.com/traex/RippleEffect/blob/master/header.png)

ExpandableLayout provides an easy way to create a view called header with an expandable view. Both view are external layout to allow a maximum of customization. [You can find a sample](https://github.com/traex/ExpandableLayout/blob/master/sample/) that how to use an ExpandableLayout to your layout.

![RippleEffect GIF](https://github.com/traex/RippleEffect/blob/master/demo.gif)

### Integration
The lib is available on Maven Central, you can find it with [Gradle, please](http://gradleplease.appspot.com/#rippleeffect)

``` xml

dependencies {
    compile 'com.github.traex.rippleeffect:library:1.3'
}

```

### Usage

#### RippleView

Declare a RippleView inside your XML layout file with a content like an ImageView or whatever.

``` xml
<com.andexert.library.RippleView
  android:id="@+id/more"
  android:layout_width="?android:actionBarSize"
  android:layout_height="?android:actionBarSize"
  android:layout_toLeftOf="@+id/more2"
  android:layout_margin="5dp"
  rv_centered="true">

  <ImageView
    android:layout_width="?android:actionBarSize"
    android:layout_height="?android:actionBarSize"
    android:src="@android:drawable/ic_menu_edit"
    android:layout_centerInParent="true"
    android:padding="10dp"
    android:background="@android:color/holo_blue_dark"/>

</com.andexert.library.RippleView>
```

If you want to know when the Ripple effect is finished, you can set a listener on your view
``` java
    rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

        @Override
        public void onComplete(RippleView rippleView) {
            Log.d("Sample", "Ripple completed");
        }

    });
```

If you want to add an `OnClickListener` don't forget to add it to the RippleView like this:
``` java
    final RippleView rippleView = (RippleView) findViewById(R.id.rippleView);
    rippleView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: onRippleViewClick
        }
    });
```


### Customization

You can change several attributes in the XML file, you have to remove "rv_" if you are using a version below v1.1.1 :

* app:rv_alpha [integer def:90 0-255] --> Alpha of the ripple
* app:rv_framerate [integer def:10] --> Frame rate of the ripple animation
* app:rv_rippleDuration [integer def:400] --> Duration of the ripple animation
* app:rv_ripplePadding [dimension def:0] --> Add a padding to the ripple
* app:rv_color [color def:@android:color/white] --> Color of the ripple
* app:rv_centered [boolean def:false] --> Center ripple in the child view
* app:rv_type [enum (simpleRipple, doubleRipple) def:simpleRipple] --> Simple or double ripple
* app:rv_zoom [boolean def:false] --> Enable zoom animation
* app:rv_zoomDuration [integer def:150] --> Duration of zoom animation
* app:rv_zoomScale [float def:1.03] --> Scale of zoom animation

For each attribute you can use getters and setters to change values dynamically.

### Troubleshooting

If you want to use the double ripple you have to set a background for the RippleView or for its child.

#### Acknowledgements

Thanks to [Google](https://www.google.com/design/spec/material-design/introduction.html) for its Material Design :)

### MIT License

```
    The MIT License (MIT)

    Copyright (c) 2014 Robin Chutaux

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
```
