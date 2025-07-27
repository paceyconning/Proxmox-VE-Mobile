package com.proxmoxmobile.presentation.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  @Override
  public MainViewModel get() {
    return newInstance();
  }

  public static MainViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MainViewModel newInstance() {
    return new MainViewModel();
  }

  private static final class InstanceHolder {
    private static final MainViewModel_Factory INSTANCE = new MainViewModel_Factory();
  }
}
