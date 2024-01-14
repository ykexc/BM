<template>
  <div>
    <el-drawer
        :model-value="props.show"
        direction="btt"
        :size="650"
        :close-on-click-modal="false"
        @close="emit('close')"
        @open="initEditor"
    >
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前,请遵守相关法律法规</div>
        </div>
      </template>
      <div style="display: flex; gap: 10px">
        <div style="width: 150px">
          <el-select v-model="editor.type" placeholder="请选择主题类型..." value-key="id" :disabled="!editor.types.length">
            <el-option v-for="item in editor.types" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"/>
                <span style="margin-left: 10px">{{item.name}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"/>
        </div>
      </div>
      <div style="margin-top: 5px;font-size: 13px;color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#dedede'"/>
        <span style="margin-left: 5px">{{editor.type ? editor.type.desc : '请在上方选择一个帖子类型'}}</span>
      </div>
      <div
          style="margin-top: 15px;
          height: 460px;overflow: hidden;border-radius: 5px"
          v-loading="editor.uploading"
          element-loading-text="正在上传图片"
      >
        <quill-editor
            v-model:content="editor.text"
            ref="editorRef"
            style="height: calc(100% - 45px)"
            content-type="delta"
            placeholder="今天想分享什么呢?"
            :options="editorOption"
        />
      </div>
      <div style="display: flex; justify-content: space-between;margin-top: 5px">
        <div style="color: grey; font-size: 13px">
          当前字数{{ contentLength }}(最大支持2324)
        </div>
        <div>
          <el-button
              type="success" :icon="Check" plain @click="submitTopic"
          >立即发表主题
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import {Check, Document} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import axios from "axios";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";


Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

get('/api/forum/topic/type', data =>  {
  editor.types = data
  console.log(editor.types)
})



const props = defineProps({
  show: {type: Boolean, required: true}
})

const emit = defineEmits<{
  (e: 'close'): void,
  (e: 'success'): void
}>()


interface Editor {
  title: string
  type: any
  text: any
  uploading: Boolean
  types: Array<any>
}

const editor = reactive<Editor>({
  title: '',
  type: null,
  text: null,
  uploading: false,
  types: []
})


const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]},
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: '-1'}, {indent: '+1'}
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      action: axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp: any) => {
        if (resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: (xhr: { setRequestHeader: (arg0: string, arg1: string) => void; }) => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }
  }
}
const deltaToText = (delta: any): string | undefined => {
  if (!delta) return ""
  let ret = ""
  for (let data of delta.ops) {
    ret += data.insert
  }
  return ret.replace(/\s/g, "")
}
const submitTopic = () => {

  const text = deltaToText(editor.text)
  if (text && text.length > 20000) {
    ElMessage.warning('字数超出限制，无法发布主题！')
    return
  }
  if (!editor.title) {
    ElMessage.warning('请填写标题！')
    return
  }
  if (!editor.type) {
    ElMessage.warning('请选择一个合适的帖子类型！')
    return
  }
  post('/api/forum/topic/create', {
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success('发文成功')
    emit('success')
    initEditor()
  })
}

const editorRef = ref()

const initEditor = () => {
  editor.title = ''
  editor.type = null
  editorRef.value.setContents('', 'user')
}

const contentLength = computed(() => {
  return deltaToText(editor.text)?.length
})


</script>

<style scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor) {
  font-size: 15px;
}

:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
</style>