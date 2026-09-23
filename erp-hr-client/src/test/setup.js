import { cleanup } from '@testing-library/react';
import { afterEach } from 'vitest';

// unmount whatever the previous test rendered (automatic only with vitest globals)
afterEach(() => cleanup());
