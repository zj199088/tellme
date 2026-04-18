/// <reference types="@dcloudio/types" />

declare namespace uni {
  function request(options: any): void;
  function uploadFile(options: any): void;
  function showToast(options: any): void;
  function getStorageSync(key: string): any;
  function setStorageSync(key: string, value: any): void;
  function removeStorageSync(key: string): void;
  function redirectTo(options: { url: string }): void;
}

declare namespace wx {
  function login(options: {
    success: (res: { code: string }) => void;
    fail: (err: any) => void;
  }): void;
  function getUserInfo(options: {
    withCredentials: boolean;
    success: (res: { userInfo: any }) => void;
    fail: (err: any) => void;
  }): void;
}

declare interface Window {
  WeixinJSBridge?: {
    invoke: (method: string, params: any, callback: (res: any) => void) => void;
    on: (event: string, callback: (res: any) => void) => void;
  };
}

declare var WeixinJSBridge: Window['WeixinJSBridge'];
