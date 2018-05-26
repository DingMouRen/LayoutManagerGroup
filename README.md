![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/img_header.png)<br><br>
### 喜欢的就点个星星吧
**gradle导入：**

* 1.项目 的build.gralde中添加

```

	allprojects{

		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```
* 2.添加依赖
```
dependencies {
	      implementation 'com.github.DingMouRen:LayoutManagerGroup:f68c9712e5'
	}
```
| EchelonLayoutManager | SlideLayoutManager |
| :----: | :---:|
|![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/layout_1.gif) | ![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/layout_3.gif)|

| SkidRightLayoutManager | SkidRightLayoutManager |
| :----: | :---:|
|![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/layout_4_1.gif) ![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/layout_4_2.gif) |  mSkidRightLayoutManager = new SkidRightLayoutManager(1.5f, 0.85f);|

| PickerLayoutManager |  
| :----: | 
|![layout_1](https://github.com/DingMouRen/LayoutManagerGroup/raw/master/picture/layout_2.gif) | 

#### 未完待你续...


