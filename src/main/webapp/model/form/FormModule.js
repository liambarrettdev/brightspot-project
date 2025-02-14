import $ from 'jquery'
import bsp_utils from 'bsp-utils'

const SECTION_SELECTOR = '.FieldSet-heading'

export class FormModule {

    constructor(el) {
        this.init()
    }

    init() {
        $(SECTION_SELECTOR).click(function(e) {
            e.preventDefault()

            const $heading = $(this)

            $heading.next().slideToggle()
            $heading.toggleClass('active')
        })
    }
}

export default bsp_utils.plugin(false, 'bsp', 'form-module', {
    '_each': function (item) {
        new FormModule($(item))
    }
});
