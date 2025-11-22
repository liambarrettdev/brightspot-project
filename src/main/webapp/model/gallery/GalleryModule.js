import $ from 'jquery'
import bsp_utils from 'bsp-utils'

export class GalleryModule {

    constructor(el) {
        this.$el = $(el)
        this.currentSlide = 1
        
        // Cache DOM elements scoped to this instance
        this.$galleryIndex = this.$el.find('.GalleryModule-index')
        this.$slides = this.$el.find('.Slide')
        this.$prevBtn = this.$el.find('.prev')
        this.$nextBtn = this.$el.find('.next')

        this.init()
    }

    init() {
        this.showSlide(this.currentSlide)

        this.$prevBtn.on('click', (e) => {
            e.preventDefault()
            this.showSlide(this.currentSlide -= 1)
        })

        this.$nextBtn.on('click', (e) => {
            e.preventDefault()
            this.showSlide(this.currentSlide += 1)
        })
    }

    showSlide(index) {
        if (index > this.$slides.length) {
            this.currentSlide = 1
        } else if (index < 1) {
            this.currentSlide = this.$slides.length
        } else {
            this.currentSlide = index
        }

        const offset = this.currentSlide - 1
        
        this.$slides.each(function() {
            $(this).css('transform', `translateX(${-100 * offset}%)`)
        })
        
        this.$galleryIndex.text(`${this.currentSlide} / ${this.$slides.length}`)
    }
}

export default bsp_utils.plugin(false, 'bsp', 'gallery-module', {
    '_each': function (item) {
        new GalleryModule($(item))
    }
});
