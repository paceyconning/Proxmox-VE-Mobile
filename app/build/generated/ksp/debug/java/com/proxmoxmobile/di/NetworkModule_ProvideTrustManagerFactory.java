package com.proxmoxmobile.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.net.ssl.X509TrustManager;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideTrustManagerFactory implements Factory<X509TrustManager> {
  @Override
  public X509TrustManager get() {
    return provideTrustManager();
  }

  public static NetworkModule_ProvideTrustManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static X509TrustManager provideTrustManager() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideTrustManager());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideTrustManagerFactory INSTANCE = new NetworkModule_ProvideTrustManagerFactory();
  }
}
