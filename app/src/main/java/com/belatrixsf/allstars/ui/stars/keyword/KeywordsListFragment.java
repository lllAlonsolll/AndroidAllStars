/* The MIT License (MIT)
* Copyright (c) 2016 BELATRIX
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
package com.belatrixsf.allstars.ui.stars.keyword;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belatrixsf.allstars.R;
import com.belatrixsf.allstars.adapters.KeywordsListAdapter;
import com.belatrixsf.allstars.entities.Keyword;
import com.belatrixsf.allstars.ui.common.AllStarsFragment;
import com.belatrixsf.allstars.ui.common.views.DividerItemDecoration;
import com.belatrixsf.allstars.utils.AllStarsApplication;
import com.belatrixsf.allstars.utils.di.modules.presenters.KeywordsListModule;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by gyosida on 5/12/16.
 */
public class KeywordsListFragment extends AllStarsFragment implements KeywordsListView, KeywordsListAdapter.KeywordListener {

    private KeywordsListAdapter keywordsListAdapter;

    @Inject KeywordsListPresenter keywordsListPresenter;

    @Bind(R.id.keywords) RecyclerView keywords;


    public KeywordsListFragment() {
        // Required empty public constructor
    }

    public static KeywordsListFragment newInstance() {
        return new KeywordsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keywords_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        keywordsListPresenter.getKeywords();
    }

    private void initViews() {
        keywordsListAdapter = new KeywordsListAdapter();
        keywordsListAdapter.setKeywordListener(this);
        keywords.setLayoutManager(new LinearLayoutManager(getActivity()));
        keywords.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), android.R.drawable.divider_horizontal_bright)));
        keywords.setAdapter(keywordsListAdapter);
    }

    @Override
    protected void initDependencies(AllStarsApplication allStarsApplication) {
        allStarsApplication
                .getApplicationComponent()
                .keywordsListComponent(new KeywordsListModule(this))
                .inject(this);
    }

    @Override
    public void deliverKeywordAsResult(Keyword keyword) {
        if (getActivity() instanceof KeywordsListListener) {
            ((KeywordsListListener) getActivity()).onKeywordSelectedForDispatching(keyword);
        }
    }

    @Override
    public void showKeywords(List<Keyword> keywords) {
        keywordsListAdapter.update(keywords);
    }

    @Override
    public void onKeywordSelected(int position) {
        keywordsListPresenter.onKeywordSelected(position);
    }

    interface KeywordsListListener {
        void onKeywordSelectedForDispatching(Keyword keyword);
    }
}