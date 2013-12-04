package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.LogDAO;
import com.zeyomir.ocfun.model.Cache;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.*;

public class LogAdd extends Activity implements AdapterView.OnItemSelectedListener{

    private ActionBar actionBar;
    private long cacheId;
    private Cache cache;

    private boolean passwordRequired = false;
    private boolean isOfFoundItType = true;
    private boolean rateFieldVisible = false;
    private boolean passwordFieldVisible = false;
    private boolean recommendFieldVisible = false;
    private boolean maintenanceFieldVisible = false;
    private boolean moreOptionsVisible = false;
    private boolean wantToRate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheId = this.getIntent().getExtras().getLong(LogDAO.cacheIdColumn);
        cache = CacheDAO.get(cacheId);
        passwordRequired = cache.requiresPassword;
        setContentView(R.layout.log_add);
        ((Spinner) findViewById(R.id.spinner)).setOnItemSelectedListener(this);

        actionBar = getSupportActionBar();
        setActionBarOptions();
        fillData();
    }

    private void setActionBarOptions() {
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.log_add);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void fillData() {
        ((TextView) findViewById(R.id.textView)).setText(cache.name);
        updateViews();
    }

    private void updateViews(){
        passwordFieldVisible = false;
        rateFieldVisible = false;
        recommendFieldVisible = false;
        maintenanceFieldVisible = moreOptionsVisible;

        if (isOfFoundItType) {
            passwordFieldVisible = passwordRequired;
            rateFieldVisible = moreOptionsVisible;
            recommendFieldVisible = moreOptionsVisible;
        }

        findViewById(R.id.editText2).setVisibility(passwordFieldVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.button).setVisibility(!moreOptionsVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.checkBox).setVisibility(rateFieldVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.ratingBar).setVisibility(rateFieldVisible && wantToRate ? View.VISIBLE : View.GONE);
        findViewById(R.id.checkBox2).setVisibility(recommendFieldVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.checkBox3).setVisibility(maintenanceFieldVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, OCFun.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_add:
                if (passwordFieldVisible && ((EditText)findViewById(R.id.editText2)).getText().length() == 0){
                    Toast.makeText(this,"Musisz podać hasło!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Toast.makeText(this,"dodawanie wpisu", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                moreOptionsVisible = true;
                break;
            case R.id.checkBox:
                wantToRate = ((CheckBox)findViewById(R.id.checkBox)).isChecked();
                break;
        }
        updateViews();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isOfFoundItType = id == 0;
        updateViews();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}