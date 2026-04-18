type AppMode = 'miniprogram' | 'web';

const DEFAULT_APP_MODE: AppMode = 'web';
const VALID_APP_MODES: AppMode[] = ['miniprogram', 'web'];

const getAppMode = (): AppMode => {
  const modeFromEnv = import.meta.env.VITE_APP_MODE as AppMode;
  if (modeFromEnv && VALID_APP_MODES.includes(modeFromEnv)) {
    return modeFromEnv;
  }
  return DEFAULT_APP_MODE;
};

const appConfig = {
  appMode: getAppMode(),
  isWeb: getAppMode() === 'web',
  isMiniprogram: getAppMode() === 'miniprogram'
};

export default appConfig;
export type { AppMode };
export { getAppMode };
