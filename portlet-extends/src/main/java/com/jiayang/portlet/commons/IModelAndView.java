package com.jiayang.portlet.commons;

import com.jiayang.portlet.exception.ViewNullExcetion;

public interface IModelAndView<T> {
  String getView() throws ViewNullExcetion;
  T getModel();
  void setView(String view);
  void setModel(T model);
}
