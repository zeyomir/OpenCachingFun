package com.zeyomir.ocfun.controller;

import android.content.Intent;
import android.os.Bundle;
import com.zeyomir.ocfun.dao.ImageDAO;
import com.zeyomir.ocfun.model.Image;

public class DisplayImage {
	public static Image getImage(Intent i) {
		Bundle b = i.getExtras();
		long id = b.getLong(ImageDAO.idColumn);
		return ImageDAO.get(id);
	}
}
