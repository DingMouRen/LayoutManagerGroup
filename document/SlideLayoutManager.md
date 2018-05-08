```
 mAdapter = new MyAdapter();
 mRecyclerView.setAdapter(mAdapter);
 mItemTouchHelperCallback = new ItemTouchHelperCallback(mRecyclerView.getAdapter(), mList);
 mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
 mSlideLayoutManager = new SlideLayoutManager(mRecyclerView, mItemTouchHelper);
 mItemTouchHelper.attachToRecyclerView(mRecyclerView);
 mRecyclerView.setLayoutManager(mSlideLayoutManager);
```
```
 mItemTouchHelperCallback.setOnSlideListener(new OnSlideListener() {
 
            @Override //滑动过程中会一直调用
            public void onSliding(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
                if (direction == ItemConfig.SLIDING_LEFT) {//向左滑动
                
                } else if (direction == ItemConfig.SLIDING_RIGHT) {//向右滑动
                
                }
            }

            @Override //滑动结束后会调用
            public void onSlided(RecyclerView.ViewHolder viewHolder, Object o, int direction) {
                if (direction == ItemConfig.SLIDED_LEFT) {//向左滑动
                    
                } else if (direction == ItemConfig.SLIDED_RIGHT) {//向右滑动
                
                }
                int position = viewHolder.getAdapterPosition();
                Log.e(TAG, "onSlided--position:" + position);
            }

            @Override //数据清空后会调用
            public void onClear() {
                
            }
        });
```