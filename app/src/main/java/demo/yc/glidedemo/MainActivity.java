package demo.yc.glidedemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Glide 框架使用
 * 1 简单用法
 *   Glide.with(context).load(image res).into(ImageView);
 *        with() :传入的是context 上下文（Activity，fragment,application,等 context);
 *             不同的context，对应图片不同的加载周期，和context 生命周期一致
 *        load(): 传入的是 图片的资源
 *           （网络地址url,文件File地址，res/drawable文件，uri地址，图片二进制数组等)；
 *        into():传入的就是ImageView 控件实例
 *
 * 2 加载占位图
 *      在加载网络图片时，因为要下载原图，所以中间会等待一段时间才显示图片
 *      这样肯会造成不好的用户体验，因此就有了占位图。
 *      就是在加载过程中，最先显示的图片，当下载好图片后，就替换掉占位图
 *      placeHolder(resId) 传入的是res/drawable 的资源文件ID;
 *
 * 3 异常占位图
 *      在加载图片失败的时候，如果没有异常占位图，则什么也不显示，造成不好体验
 *      在图片加载失败后，就显示异常占位图，提示用户无法显示之类的图片
 *      error(resId) 传入的是res/drawable 的资源文件ID
 *
 *      注意：和加载占位图的逻辑是基本一致的。一个在加载过程中显示，一个在加载失败后显示
 *      可以同时设置加载占位图和异常占位图
 *
 * 4 加载gif 动态图（Glide 的独特点）
 *      和加载普通图片一样使用，只要load 传入gif 图片地址就行
 *      ( glide 还能加载视频文件的第一帧画面 )
 *      测试失败
 *      网上说是没有配置缓存策略，默认是缓存全部，到时加载时间过长，没办法显示
 *      因为不配默认就是ALL，这种情况下会把GIF图的每一帧都去压缩然后缓存，
 *      时间极长，可能要几分钟gif图才会显示出来。
 *      解决，设置disCacheStrategy(DisCacheStrategy.NONE)  不缓存
 *      disCacheStrategy(DisCacheStrategy.SOURCE)  只缓存原图
 *
 *
 * 5 强制加载静态图（动态图）
 *      因为Glide 非常强大，传入地址，不加格式，他也能判断是动态还是静态
 *      如果，需要确保加载的是静态图，则使用 asBitmap(),如果传入是静态图片则正常加载，
 *      如果是gif 图片，则指显示第一帧图片
 *       如果需要确保加载的是动态图，则使用 asGif(),如果传入的是静态图，则加载失败
 *       如果是gif 图片，则正常加载
 *
 * 6 加载图片的淡入动画
 *      时间
 *          显示图片的默认动画是300毫秒的
 *          可以通过crossFade(time)来改变时间
 *          也可以通过dontAnimate() 来取消动画
 *      动画类型
 *          默认应该是alpha  0 到 1 变化的
 *          可以通过crossFade(animId,time) 来改变；animId 是动画资源Id
 *          但是，如果设置了加载占位图，则动画效果无法改变。
 *
 * 7  加载本地文件
 *      只要修改load() 传入的参数，改为 filepath 即可。
 *      问题：如果是修改头像的功能，利用相同的path  去保存新的图片，每次都覆盖就图片
 *           每次更换头像后重新glide.with().load().into(),发现头像不会变成新的
 *           这是因为 glide 已经对该path 缓存了，
 *           不管源图片有没有改变，都是显示最开始缓存的图片
 *      解决： 设置skipMemoryCache（true) 表示不用内存缓存
 *             设置diskCacheStrategy(DisCacheStrategy.NONE); 表示不用磁盘缓存
 *             这样，每次重新加载图片时，都会去原地址寻找，
 *             虽然效率相对慢，但也是一种方法
 *
 *
 * 8 缓存策略（内存缓存  磁盘缓存）
 *     （1）缓存图片，避免每次加载都要原地址加载图片，提高运行效率
 *     （2）glide和三级缓存一样
 *          最开始在memory 寻找
 *          然后 disk 寻找
 *          最后 网络 （原地址）
 *          即  主存  >  磁盘  >  网络（原地址）
 *
 *      （3）首先是图片资源：分为原图（没有压缩过的）
 *          处理后的图片（即压缩过的，用来显示的）
 *
 *     （4）内存缓存：skipMemoryCache(true|false)  默认缓存的是 处理后的图片
 *               true: 不用主存缓存
 *               false: 用主存缓存（默认）
 *
 *      （5）磁盘缓存：diskCacheStrategy(对应的策略)  默认是两种图片都缓存的；
 *               DiskCacheStrategy.NONE:  什么也不缓存
 *               DiskCacheStrategy.SOURCE: 缓存 原图片
 *               DiskCacheStrategy.ALL:    两种都缓存（默认）
 *               DiskCacheStrategy.RESULT: 缓存 处理后的图片
 *
 *      （6） 因为 根据三级缓存机制，主存和磁盘缓存的策略可以随意搭配使用
 *                根据需要就好
 *
 *       （7） 删除缓存
 *             删除所有内存缓存
 *              Glide.get(this).clearMemory();
 *              删除所有的磁盘缓存（需要在子线程中操作）
 *              Glide.get(this).clearDiskCache();
 *
 *
 *
 *
 */
public class MainActivity extends AppCompatActivity
{
    ImageView[] imags;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imags = new ImageView[]{
                (ImageView) findViewById(R.id.image1),
                (ImageView) findViewById(R.id.image2),
                (ImageView) findViewById(R.id.image3),
                (ImageView) findViewById(R.id.image4),
                (ImageView) findViewById(R.id.image5)
        };
    }

    /**
     * 加载网络图片,需要添加网络权限。
     * @param view
     */
    public void click1(View view){
        String url = "http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg";
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imags[0]);

    }

    /**
     * 加载图片过程中，修改动画
     * @param view
     */
    public void click2(View view){
        String url = "http://img02.tooopen.com/images/20140504/sy_60294738471.jpg";
        Glide.with(this).load(url)
                .crossFade(R.anim.glide_in,1000)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imags[1]);
        Glide.get(this).clearMemory();
        Glide.get(this).clearDiskCache();
    }

    /**
     * 配合加载和异常占位图
     * @param view
     */
    public void click3(View view){
        String url = "http://img2.imgtn.bdimg.com/it/u=819201812,3553302270&fm=214&gp=0.jpg";
        Glide.with(this).load(url)
                .placeholder(R.drawable.wait)
                .error(R.drawable.error)
                .into(imags[2]);
    }

    /**
     * 加载gif 图片，
     * @param view
     *
     */
    public void click4(View view){
        String url = "http://p1.pstatp.com/large/166200019850062839d3";
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imags[3]);
    }

    public void click5(View view){
       String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/809/123/head/aaa.jpg";
        Glide.with(this)
                .load(path)
                .placeholder(R.drawable.wait)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imags[4]);
    }





}
