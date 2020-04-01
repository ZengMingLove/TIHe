package com.zbht.hgb.ui.mine

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.core.tools.SPUtil
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.zbht.hgb.R
import com.zbht.hgb.base.BaseStatusActivity
import com.zbht.hgb.common.Constant
import com.zbht.hgb.network.RetrofitService
import com.zbht.hgb.network.Transformer
import com.zbht.hgb.ui.home.bean.GoodsBean
import com.zbht.hgb.ui.mine.bean.TaoGoodBean
import com.zbht.hgb.ui.store.GoodDetialActivity
import com.zbht.hgb.util.decoration.VItemDecoration
import kotlinx.android.synthetic.main.activity_collect_good.*
import org.json.JSONArray

/**
 * 我的收藏
 */
class CollectGoodActivity : BaseStatusActivity(), OnRefreshListener, OnLoadMoreListener {

    private val goodData: ArrayList<GoodsBean> = ArrayList()
    private val taoGoodData: ArrayList<TaoGoodBean> = ArrayList()

    private var goodAdapter: BaseQuickAdapter<*, *>? = null

    override fun initView(): Int {
        return R.layout.activity_collect_good
    }

    override fun initData() {
        setTitleView("我的收藏")
        srl_view.setOnRefreshListener(this)
        srl_view.setOnLoadMoreListener(this)
        srl_view.isEnableLoadMore = false

        initAdapter()
        initGoodData(true)

    }

    override fun inNetWork() {
        initGoodData(true)
    }

    override fun onRefresh(refreshLayout: RefreshLayout?) {
        initGoodData(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout?) {

    }

    /**
     * 初始列表化适配器
     */
    private fun initAdapter() {
        goodAdapter = object : BaseQuickAdapter<GoodsBean, BaseViewHolder>(R.layout.item_collect_tao_good, goodData) {
            override fun convert(helper: BaseViewHolder, item: GoodsBean) {
                if (item.images != null) {
                    val imagesArray = JSONArray(item.images)
                    Glide.with(mContext).load(imagesArray.get(0))
                        .into(helper.getView<View>(R.id.iv_tao_goods_pic) as ImageView)
                }

                helper.setText(R.id.iv_tao_good_name, item.title)
                helper.setText(R.id.tv_tao_good_price, item.salesPrice.toString())

                helper.getView<View>(R.id.rl_collect_tao_good_item_root).setOnClickListener {
                    val intent = Intent(mContext, GoodDetialActivity::class.java)
                    intent.putExtra("goodId", item.commodityId)
                    startActivity(intent)
                }

                helper.getView<View>(R.id.tv_cancle_collect).setOnClickListener {
                    cancelGoodCollect(item)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        rv_good_collect.layoutManager = layoutManager
        rv_good_collect.addItemDecoration(VItemDecoration(ConvertUtils.dp2px(14.33f), ConvertUtils.dp2px(9.33f)))
        rv_good_collect.adapter = goodAdapter
    }

    /**
     * 获取收藏商品列表
     */
    private fun initGoodData(isFirst: Boolean) {
        if (isFirst) {
            showLoadingDialog()
        }
        addDispose(
            RetrofitService.getInstance().createShowApi()
                .getCollectGoodList(SPUtil.get(this, Constant.SPKey.TOKEN, "") as String)
                .compose(Transformer.io_main())
                .subscribe({
                    if (isFirst) {
                        hideLoadingDialog()
                    } else {
                        srl_view.finishRefresh()
                    }
                    if (it.data.size == 0) {
                        showEmpty()
                    } else {
                        hideStateLayout()
                        goodData.clear()
                        goodData.addAll(it.data)
                        goodAdapter?.notifyDataSetChanged()
                    }
                }, {
                    if (isFirst) {
                        hideLoadingDialog()
                    } else {
                        srl_view.finishRefresh()
                    }
                    Log.e(TAG, "${it.message}")
                })
        )
    }

    /**
     * 获取收藏列表
     */
    private fun initTaoGoodData() {
        showLoadingDialog()
        addDispose(
            RetrofitService.getInstance().createShowApi()
                .getTaoGoodCollectList(SPUtil.get(this, Constant.SPKey.TOKEN, "") as String)
                .compose(Transformer.io_main())
                .subscribe({
                    hideLoadingDialog()
//                    rv_good_collect.adapter = taoGoodAdapter
//                    taoGoodData.clear()
//                    taoGoodData.addAll(it.data)
//                    taoGoodAdapter.notifyDataSetChanged()
                }, {
                    hideLoadingDialog()
                    Log.e(TAG, "${it.message}")
                })
        )
    }

    /**
     * 商品取消收藏
     */
    private fun cancelGoodCollect(bean: GoodsBean) {
        val mQueryMap: HashMap<String, Any> = HashMap()
        mQueryMap["commodity_id"] = bean.commodityId
        showLoadingDialog()
        addDispose(
            RetrofitService.getInstance().createShowApi()
                .cancelCollectGood(mQueryMap)
                .compose(Transformer.io_main())
                .subscribe({
                    hideLoadingDialog()
                    goodData.remove(bean)
                    goodAdapter?.notifyDataSetChanged()
                    if (goodData.size < 1) {
                        showEmpty()
                    }
                }, {
                    hideLoadingDialog()
                    showToast(it.message)
                })
        )
    }
}
