import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import { defineConfig, globalIgnores } from 'eslint/config';
import stylistic from '@stylistic/eslint-plugin';

const DEFAULT_TAB_SIZE = 4;

export default defineConfig([
    globalIgnores(['dist']),
    {
        files: ['**/*.{js,jsx}'],
        plugins: {
            '@stylistic': stylistic,
        },
        extends: [
            js.configs.recommended,
            reactHooks.configs['recommended-latest'],
            reactRefresh.configs.vite,
        ],
        languageOptions: {
            ecmaVersion: 2020,
            globals: globals.browser,
            parserOptions: {
                ecmaVersion: 'latest',
                ecmaFeatures: { jsx: true },
                sourceType: 'module',
            },
        },
        rules: {
            'no-unused-vars': ['error', { varsIgnorePattern: '^[A-Z_]' }],  // only allow unused vars that start with uppercase or underscore
            'no-magic-numbers': ['warn', { ignore: [-1, 0, 1] }],	        // disallow magic numbers (except -1, 0, 1)
            'eqeqeq': ['error', 'always'],                                  // require use of strict equals (=== and !==)
            'curly': ['error', 'all'],		                                // require braces for control statements (if/else/for/while)
            'brace-style': ['error', '1tbs', { allowSingleLine: false }],   // enforce one true brace style
            'no-var': 'error',										        // require let or const (disallow var))
            'prefer-const': 'error',									    // require const for variables that are never reassigned
            'no-duplicate-imports': 'error',							    // disallow duplicate imports
            'no-console': ['warn', { allow: ['warn', 'error'] }],	        // disallow console.log (allow warn/error)

            '@stylistic/indent': ['error', DEFAULT_TAB_SIZE],			    // enforce consistent indentation (4 spaces)
            '@stylistic/quotes': ['error', 'single'],					    // enforce single quotes
            '@stylistic/semi': ['error', 'always'],					        // require semicolons at the end of statements
            '@stylistic/eol-last': ['error', 'always'],				        // require newline at the end of file
            '@stylistic/no-multiple-empty-lines': ['error', { max: 1 }],	// disallow multiple empty lines
            '@stylistic/object-curly-spacing': ['error', 'always'],		    // require space inside object braces
            '@stylistic/array-bracket-spacing': ['error', 'never'],		    // disallow space inside array brackets
        },
    },
]);
