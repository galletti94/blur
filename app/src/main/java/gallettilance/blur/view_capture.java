package gallettilance.blur;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Color;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.lang.Integer;

public class view_capture extends AppCompatActivity {

    ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_capture);
        Intent intent = getIntent();
        final Bitmap bitmapPicture = intent.getParcelableExtra("BitmapImage");
        final double[][] inputIMG = new double[1][28 * 28];

        image_view = findViewById(R.id.image_view);
        image_view.setImageBitmap(bitmapPicture);

        findViewById(R.id.yesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final StringBuilder img = new StringBuilder(bitmapPicture.getWidth() * bitmapPicture.getHeight());

                for(int i=0; i < bitmapPicture.getWidth(); i++)
                {
                    for(int j=0; j < bitmapPicture.getHeight(); j++)
                    {
                        int colour = bitmapPicture.getPixel(i, j);
                        int red = Color.red(colour);
                        int blue = Color.blue(colour);
                        int green = Color.green(colour);

                        DecimalFormat df = new DecimalFormat("#.##");
                        double myRGB = Double.valueOf(df.format((red + green + blue)/3.0));
                        inputIMG[0][i*bitmapPicture.getWidth() + j] = (.99 * myRGB / 255.0) + .01;

                        img.append(df.format((.99 * myRGB / 255.0) + .01));
                        img.append(',');
                    }
                }

                double[][] pred;

                NeuralNetwork myNN = new NeuralNetwork(0,0,0,0);
                myNN.initializeFromAPI();

                pred = myNN.query(inputIMG);
                int max = 0;

                for (int i=0; i < pred.length; i++) {
                    if (pred[i][0] > pred[max][0]) {
                        max = i;
                    }
                }

                final int myprediction = max;

                new AlertDialog.Builder(view_capture.this)
                        .setTitle("Prediction")
                        .setMessage(Integer.toString(myprediction))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String img_label = Integer.toString(myprediction);
                                String img_type = "digit";

                                String myUrl = "https://rest-blur.herokuapp.com/images/";
                                HttpPOSTRequest postRequest = new HttpPOSTRequest();

                                try {
                                    postRequest.execute(myUrl, img.toString(), img_label, img_type);
                                } catch(Exception e) {
                                    Log.d("Error", e.toString());
                                }

                                Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Sent to DB");
                                startActivity(intent);

                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //ask for user input
                                AlertDialog.Builder alert = new AlertDialog.Builder(view_capture.this);

                                alert.setTitle("What digit was it? (0-9)");

                                final EditText input = new EditText(view_capture.this);
                                alert.setView(input);

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        String img_label = input.getText().toString();
                                        String img_type = "digit";

                                        String myUrl = "https://rest-blur.herokuapp.com/images/";
                                        HttpPOSTRequest postRequest = new HttpPOSTRequest();

                                        try {
                                            postRequest.execute(myUrl, img.toString(), img_label, img_type);
                                        } catch(Exception e) {
                                            Log.d("Error", e.toString());
                                        }

                                        Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Sent to DB - Thank you!");
                                        startActivity(intent);
                                    }
                                });

                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Not Sent");
                                        startActivity(intent);
                                    }
                                });

                                alert.show();

                            }}).show();

            }
        });


        findViewById(R.id.noButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view_capture.this, capture.class).putExtra("text", "Try again");
                startActivity(intent);
        }});

    }
}
