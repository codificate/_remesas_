package net.iantech.android.remitee;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;

import net.iantech.android.remitee.model.Party;
import net.iantech.android.remitee.util.CaptureAndCropImage;
import net.iantech.android.remitee.util.GsonSingleton;
import net.iantech.android.remitee.util.SnackBar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static net.iantech.android.remitee.SendActivity.CONTACT_INFO_FRAGMENT;

/**
 * Created by andres on 27/03/17.
 */

public class TakePhotoCredentials extends Fragment {

    private final String LOG_TAG = TakePhotoCredentials.class.getSimpleName();
    private RemiteeApp app;
    private Party party;

    private static final String ISSENDER_PARAM = "isSender";
    public String uriProfilePhotoTaked = null;
    private Boolean isSender;
    private CaptureAndCropImage mCaptureAndCropImage;

    private ImageView frontpicturedocument;
    private ImageView backpicturedocumenttxt;
    private static Bitmap frontPictureBitmap, backPictureBitmap;


    public TakePhotoCredentials(){}

    public static TakePhotoCredentials newInstance(Boolean _isSender) {
        TakePhotoCredentials fragment = new TakePhotoCredentials();
        Bundle args = new Bundle();
        args.putBoolean(ISSENDER_PARAM, _isSender);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        app = ((RemiteeActivity) getActivity()).app;

        if (getArguments() != null) {
            isSender = getArguments().getBoolean(ISSENDER_PARAM);

            try {
                //Obtengo desde el bundle el objeto party seleccionado
                party = GsonSingleton.getInstance().getGson().fromJson( getArguments().getString( "partySenderSelected" ), Party.class);
                //party = Party.findById( Party.class, 1 );
            } catch ( Exception e ) {
                party = null;
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View takePhotoCredentials = inflater.inflate(R.layout.fragment_take_photo_credentials, container, false);

        frontpicturedocument.setOnClickListener( new OnFrontClick() );

        backpicturedocumenttxt.setOnClickListener( new OnBackClick() );

        return takePhotoCredentials;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SendActivity.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                captureResult(data);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri selectedImageUri = result.getUri();
                mCaptureAndCropImage = new CaptureAndCropImage(getActivity());
                Bitmap bitmap = mCaptureAndCropImage.getBitmap(selectedImageUri);
                this.saveImage(selectedImageUri, bitmap);
            }
        } else if (resultCode == RESULT_CANCELED) { // user cancelled Image capture
            SnackBar.makeMsg(getString(R.string.error_user_cancelled), this.getView() );
        } else { // failed to capture image
            SnackBar.makeMsg(getString(R.string.error_failed_capture), this.getView());
        }
    }

    public void captureResult(Intent data) {

        mCaptureAndCropImage = new CaptureAndCropImage(getActivity());
        mCaptureAndCropImage.captureResult(data);
    }

    public void saveImage(Uri selectedImageUri, Bitmap bitmap) {

        Drawable d = new BitmapDrawable(getResources(), bitmap);

        if (selectedImageUri != null)
            this.uriProfilePhotoTaked = selectedImageUri.toString();

        frontpicturedocument.setImageDrawable(d);
        frontPictureBitmap = bitmap;
    }

    private class OnFrontClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ((SendActivity) getActivity()).checkCameraPermission();
        }
    }

    private class OnBackClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ((SendActivity) getActivity()).checkCameraPermission();
        }
    }

    @Override
    public void onStart() {
        getActivity().setTitle("Foto del documento");
    }
}
