package me.vouch4.app.adapters;

import android.view.View;
import android.widget.RelativeLayout;
import com.stfalcon.chatkit.messages.MessageHolders;

import me.vouch4.app.R;
import me.vouch4.app.models.ChatMessage;

public class CustomOutcomingHolder extends MessageHolders.OutcomingImageMessageViewHolder<ChatMessage> {

    private RelativeLayout progressBar;


    public CustomOutcomingHolder(View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        if (message.isLoading()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }
}
