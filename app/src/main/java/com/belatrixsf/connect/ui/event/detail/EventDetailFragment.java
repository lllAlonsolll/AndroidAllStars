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
package com.belatrixsf.connect.ui.event.detail;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.belatrixsf.connect.R;
import com.belatrixsf.connect.entities.Event;
import com.belatrixsf.connect.ui.common.BelatrixConnectFragment;
import com.belatrixsf.connect.utils.BelatrixConnectApplication;
import com.belatrixsf.connect.utils.di.modules.presenters.EventDetailPresenterModule;
import com.belatrixsf.connect.utils.media.ImageFactory;
import com.belatrixsf.connect.utils.media.loaders.ImageLoader;

import butterknife.Bind;

/**
 * Created by icerrate on 27/06/2016.
 */
public class EventDetailFragment extends BelatrixConnectFragment implements EventDetailView {

    public static final String EVENT_ID_KEY = "_event_id_key";
    public static final String EVENT_KEY = "_event_key";

    private EventDetailPresenter eventDetailPresenter;

    @Bind(R.id.date) TextView dateTextView;
    @Bind(R.id.location) TextView locationTextView;
    @Bind(R.id.title) TextView titleTextView;
    @Bind(R.id.description) TextView descriptionTextView;
    @Bind(R.id.collaborators) TextView collaboratorsCountTextView;
    @Bind(R.id.participants) TextView participantsCountTextView;

    public static EventDetailFragment newInstance(Integer eventId) {
        Bundle bundle = new Bundle();
        if (eventId != null) {
            bundle.putInt(EventDetailActivity.EVENT_ID_KEY, eventId);
        }
        EventDetailFragment accountFragment = new EventDetailFragment();
        accountFragment.setArguments(bundle);
        return accountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        Integer eventId;
        boolean hasArguments = (getArguments() != null && getArguments().containsKey(EventDetailActivity.EVENT_ID_KEY));
        if (savedInstanceState != null) {
            restorePresenterState(savedInstanceState);
        } else if (hasArguments) {
            eventId = getArguments().getInt(EventDetailActivity.EVENT_ID_KEY);
            eventDetailPresenter.setEventId(eventId);
        }
        eventDetailPresenter.loadEventDetail();
    }

    private void initViews() {
    }

    @Override
    protected void initDependencies(BelatrixConnectApplication belatrixConnectApplication) {
        eventDetailPresenter = belatrixConnectApplication.getApplicationComponent()
                .eventDetailComponent(new EventDetailPresenterModule(this))
                .eventDetailPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        savePresenterState(outState);
        super.onSaveInstanceState(outState);
    }

    private void restorePresenterState(Bundle savedInstanceState) {
        Event event = savedInstanceState.getParcelable(EVENT_KEY);
        Integer eventId = savedInstanceState.getInt(EVENT_ID_KEY, 0);
        eventDetailPresenter.setEventId(eventId);
        eventDetailPresenter.load(event);
    }

    private void savePresenterState(Bundle outState) {
        outState.putInt(EVENT_ID_KEY, eventDetailPresenter.getEventId());
        outState.putParcelable(EVENT_KEY, eventDetailPresenter.getEvent());
    }

    @Override
    public void showComments() {
        //TODO
    }

    private void startPostponedEnterTransition() {
        if (pictureImageView == null) {
            return;
        }
        pictureImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                pictureImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(getActivity());
                return false;
            }
        });
    }

    @Override
    public void showDateTime(String dateTime) {
        dateTextView.setText(dateTime);
    }

    @Override
    public void showLocation(String location) {
        locationTextView.setText(location);
    }

    @Override
    public void showTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void showDescription(String description) {
        descriptionTextView.setText(description);
    }

    @Override
    public void showCollaboratorsCount(String collaboratorsCount) {
        collaboratorsCountTextView.setText(collaboratorsCount);
    }

    @Override
    public void showParticipantsCount(String participantsCount) {
        participantsCountTextView.setText(participantsCount);
    }

    @Override
    public void showPicture(final String profilePicture) {
        ImageFactory.getLoader().loadFromUrl(
                profilePicture,
                pictureImageView,
                ImageLoader.ImageTransformation.BORDERED_CIRCLE,
                new ImageLoader.Callback() {
                    @Override
                    public void onSuccess() {
                        startPostponedEnterTransition();
                    }

                    @Override
                    public void onFailure() {
                        startPostponedEnterTransition();
                    }
                },
                pictureImageView.getResources().getDrawable(R.drawable.contact_placeholder)
        );
    }

    @Override
    public void onDestroyView() {
        eventDetailPresenter.cancelRequests();
        super.onDestroyView();
    }
}