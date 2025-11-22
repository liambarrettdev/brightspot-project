import $ from 'jquery'
import bsp_utils from 'bsp-utils'

export class FormModule {

    constructor(el) {
        this.$el = $(el)
        this.init()
    }

    init() {
        this.$el.find('.FieldSet-heading').on('click', (e) => {
            e.preventDefault()

            const $heading = $(e.currentTarget)
            const $inputs = $heading.next('.FieldSet-inputs')
            const isExpanded = $heading.attr('aria-expanded') === 'true'

            $inputs.slideToggle()
            $heading.toggleClass('active')
            
            $heading.attr('aria-expanded', !isExpanded)
            $inputs.attr('aria-hidden', isExpanded)
        })
    }
}

export default bsp_utils.plugin(false, 'bsp', 'form-module', {
    '_each': function (item) {
        new FormModule($(item))
    }
});
