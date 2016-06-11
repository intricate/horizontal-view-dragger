package org.demo.horizontalviewdragger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample usage...
        HorizontalViewDragger horizontalViewDragger = (HorizontalViewDragger)findViewById(R.id.horizontalViewDragger);
        horizontalViewDragger.setDraggableView(findViewById(R.id.draggableView));
        horizontalViewDragger.setLeftDragEnabled(true);
        horizontalViewDragger.setRightDragEnabled(true);
        horizontalViewDragger.setOnDragCompleteListener(new HorizontalViewDragger.OnDragCompleteListener() {
            @Override
            public void onDragCompleted(int direction){
                String str = "Drag Complete: " + ((direction == HorizontalViewDragger.Direction.LEFT) ? "Left" : "Right");
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
