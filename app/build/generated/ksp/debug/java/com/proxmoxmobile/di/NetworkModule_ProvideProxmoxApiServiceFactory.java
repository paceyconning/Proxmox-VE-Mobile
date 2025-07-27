package com.proxmoxmobile.di;

import com.proxmoxmobile.data.api.ProxmoxApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideProxmoxApiServiceFactory implements Factory<ProxmoxApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideProxmoxApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ProxmoxApiService get() {
    return provideProxmoxApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideProxmoxApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideProxmoxApiServiceFactory(retrofitProvider);
  }

  public static ProxmoxApiService provideProxmoxApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideProxmoxApiService(retrofit));
  }
}
