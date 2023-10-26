'use strict';

module.exports = function (grunt) {
    require('bsp-grunt')(grunt, {
        bsp: {

            styles: {
                dir: '',
                less: [ '*.less' ],
                options: {
                    autoprefixer: true
                }
            },

            scripts: {
                dir: ''
            },

            systemjs: {
                configOverrides: {
                    minify: true
                },
                srcFile: '<%= bsp.scripts.devDir %>' + 'All.js',
                destFile: '<%= bsp.scripts.minDir %>' + 'All.min.js'
            }
        },

        copy: {
            compiledCSS: {
                files: [
                    {
                        src: ['**/*<%= bsp.styles.ext %>', '**/*.css']
                    }
                ]
            }
        },

        systemjs: {
            dist: {
                files: [
                    { src:'<%= bsp.systemjs.srcFile %>', dest:'<%= bsp.systemjs.destFile %>' }
                ]
            }
        },

        watch: {
            less: {
                files: ['*.less', '<%= bsp.styles.srcDir %>' + '/**/*.less', '<%= bsp.styles.srcDir %>' + '/**/*.vars'],
                tasks: ['bsp-config-dest', 'copy:styles', 'less:compile', 'bsp-autoprefixer', 'copy:compiledCSS']
            }
        }
    });
};
