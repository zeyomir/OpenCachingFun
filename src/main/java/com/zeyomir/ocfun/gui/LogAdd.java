package com.zeyomir.ocfun.gui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.controller.AddLog;
import com.zeyomir.ocfun.dao.CacheDAO;
import com.zeyomir.ocfun.dao.InternalResourceMapper;
import com.zeyomir.ocfun.dao.LogDAO;
import com.zeyomir.ocfun.model.Cache;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.*;

import java.util.HashMap;
import java.util.Map;

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
    private long spinnerSelectedId;

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
                (new AddLog(this)).add(gatherData());
                Toast.makeText(this,"Dodawanie wpisu", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Map<String, String> gatherData() {
        Map<String, String> data = new HashMap<String, String>();

        long cacheId = cache.id;
        String cacheCode = cache.code;
        String cacheName = cache.name;
        String message = ((EditText)findViewById(R.id.editText)).getText().toString();

        int type;
        if (isOfFoundItType)
            type = InternalResourceMapper.found.id();
        else if (spinnerSelectedId == 1)
            type = InternalResourceMapper.notfound.id();
        else
            type = InternalResourceMapper.comment.id();

        String password;
        if (passwordFieldVisible)
            password = ((EditText)findViewById(R.id.editText2)).getText().toString();
        else
            password = null;

        int rating;
        if (rateFieldVisible && wantToRate)
            rating = (int) ((RatingBar)findViewById(R.id.ratingBar)).getRating();
        else
            rating = 0;

        boolean recommendation = ((CheckBox)findViewById(R.id.checkBox2)).isChecked();
        boolean needsMaintenance = ((CheckBox)findViewById(R.id.checkBox3)).isChecked();

        data.put("cacheId", ""+cacheId);
        data.put("cacheCode", cacheCode);
        data.put("cacheName", cacheName);
        data.put("message", message);
        data.put("type", ""+type);
        data.put("password", password);
        data.put("rating", ""+rating);
        data.put("recommendation", ""+recommendation);
        data.put("needsMaintenance", ""+needsMaintenance);

        return data;
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
        spinnerSelectedId = id;
        isOfFoundItType = id == 0;
        updateViews();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}