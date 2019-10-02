![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img_header.png)<br><br>
### Just like a star.

**Gradle import:**

1. Add in the Root level `build.gradle`:

```
	allprojects{

		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add Dependency:
```
dependencies {
	     implementation 'com.github.DingMouRen:LayoutManagerGroup:1e6f4f96eb'
	}
```

| EchelonLayoutManager | SkidRightLayoutManager | SlideLayoutManager |
| :----: | :---:| :---:|
|![img1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img1.gif) |  ![img2](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img2.gif)| ![img3](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img3.gif)|

| PickerLayoutManager | BannerLayoutManager | ViewPagerLayoutManager |
| :---:| :---:|:---:|
|  ![img4](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img4.gif)|![img5](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img5.gif)|![img6](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img6.gif)|

## License
```
Copyright (C) 2018 DingMouRen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
```

