package com.renren.breadtravel.widget.easytagdragview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.renren.breadtravel.R;
import com.renren.breadtravel.widget.easytagdragview.adapter.AbsTipAdapter;
import com.renren.breadtravel.widget.easytagdragview.adapter.AddTipAdapter;
import com.renren.breadtravel.widget.easytagdragview.adapter.DragTipAdapter;
import com.renren.breadtravel.widget.easytagdragview.bean.Tip;
import com.renren.breadtravel.widget.easytagdragview.widget.DragDropGirdView;
import com.renren.breadtravel.widget.easytagdragview.widget.TipItemView;

import java.util.ArrayList;
import java.util.List;


public class EasyTipDragView extends RelativeLayout
        implements AbsTipAdapter.DragDropListener, TipItemView.OnDeleteClickListener, View.OnClickListener {
    private DragDropGirdView dragDropGirdView;
    private GridView addGridView;
    private ImageView closeImg;
    private TextView completeTv;
    private AddTipAdapter addTipAdapter;
    private DragTipAdapter dragTipAdapter;
    private OnDataChangeResultCallback dataResultCallback;
    private OnCompleteCallback completeCallback;
    private OnCompleteLastDataCallback mLastDataCallback;
    private ArrayList<Tip> lists;
    private List<Tip> lastTips = new ArrayList<>();
    private boolean isOpen = false;
    private Context mContext;

    public EasyTipDragView(Context context) {
        super(context);
        initView();
        this.mContext = context;
    }

    public EasyTipDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        this.mContext = context;
    }

    public EasyTipDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EasyTipDragView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
        this.mContext = context;
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        close();
        dragTipAdapter = new DragTipAdapter(getContext(), this, this);
        dragTipAdapter.setTilesStartLimit(0);  //?????????????????????????????????
        dragTipAdapter.setFirtDragStartCallback(new DragTipAdapter.OnFirstDragStartCallback() {
            @Override
            public void firstDragStartCallback() {
                //?????????????????????item????????????
                closeImg.setVisibility(View.GONE);
                completeTv.setText(mContext.getResources().getString(R.string.finish));
                completeTv.setVisibility(View.VISIBLE);
            }
        });
        addTipAdapter = new AddTipAdapter();
        //??????view
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_easytagdrag, this);
        closeImg = (ImageView) view.findViewById(R.id.drag_close_img);
        completeTv = (TextView) view.findViewById(R.id.drag_finish_tv);
        dragDropGirdView = (DragDropGirdView) view.findViewById(R.id.tagdrag_view);
        dragDropGirdView.getDragDropController().addOnDragDropListener(dragTipAdapter);

        dragDropGirdView.setDragShadowOverlay((ImageView) view.findViewById(R.id.tile_drag_shadow_overlay));
        dragDropGirdView.setAdapter(dragTipAdapter);
        addGridView = (GridView) view.findViewById(R.id.add_gridview);
        addGridView.setAdapter(addTipAdapter);
        addGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dragTipAdapter.getData().add(addTipAdapter.getData().get(position));
                dragTipAdapter.refreshData();
                addTipAdapter.getData().remove(position);
                addTipAdapter.refreshData();
                lastTips = addTipAdapter.getData();
            }
        });
        closeImg.setOnClickListener(this);
        completeTv.setOnClickListener(this);
    }

    @Override
    public DragDropGirdView getDragDropGirdView() {
        return dragDropGirdView;
    }

    @Override
    public void onDataSetChangedForResult(ArrayList<Tip> lists) {
        this.lists = lists;
        if (dataResultCallback != null) {
            dataResultCallback.onDataChangeResult(lists);
        }
    }

    @Override
    public void onDeleteClick(Tip entity, int position, View view) {
        addTipAdapter.getData().add(entity);
        lastTips.add(entity);
        addTipAdapter.refreshData();
        dragTipAdapter.getData().remove(position);
        dragTipAdapter.refreshData();
    }

    public void setDragData(List<Tip> tips) {
        dragTipAdapter.setData(tips);
    }

    public void setAddData(List<Tip> tips) {
        if (tips == null)
            tips = new ArrayList<>();
        lists = new ArrayList<>(tips);
        lastTips.addAll(lists);
        addTipAdapter.setData(tips);
    }

    public void setDataResultCallback(OnDataChangeResultCallback dataResultCallback) {
        this.dataResultCallback = dataResultCallback;
    }

    public void setOnCompleteCallback(OnCompleteCallback callback) {
        this.completeCallback = callback;
    }

    public void setSelectedListener(TipItemView.OnSelectedListener selectedListener) {
        dragTipAdapter.setItemSelectedListener(selectedListener);
    }

    public void setLastDataCallback(OnCompleteLastDataCallback lastDataCallback) {
        mLastDataCallback = lastDataCallback;
    }

    public void close() {
        setVisibility(View.GONE);
        isOpen = false;
    }

    public void open() {
        setVisibility(View.VISIBLE);
        isOpen = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.drag_close_img:
                //????????????????????????
                //close();

                break;
            case R.id.drag_finish_tv:
                //???????????????????????????
                dragTipAdapter.cancelEditingStatus();
                if (completeCallback != null) {
                    completeCallback.onComplete(lists);
                }
                if (mLastDataCallback != null) {
                    mLastDataCallback.lastDataComplete(lastTips);
                }
                break;
        }
    }

    //????????????????????????,??????????????????item????????????
    public interface OnDataChangeResultCallback {
        void onDataChangeResult(ArrayList<Tip> tips);
    }

    //???????????????"??????"??????EasyTipDragView?????????
    public interface OnCompleteCallback {
        void onComplete(ArrayList<Tip> tips);
    }

    //???????????????"??????"??????EasyTipDragView??????????????????????????????item
    public interface OnCompleteLastDataCallback {
        void lastDataComplete(List<Tip> tips);
    }

    public boolean isOpen() {
        return isOpen;
    }

    //?????????????????????
    public boolean onKeyBackDown() {
        //????????????????????????????????????????????????
        if (dragTipAdapter.isEditing()) {
            dragTipAdapter.cancelEditingStatus();
            return true;
        } else {
            //?????????view
            close();
            return false;
        }
    }
}
