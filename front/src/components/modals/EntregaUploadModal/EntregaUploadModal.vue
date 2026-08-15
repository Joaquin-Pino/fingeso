<script setup>
import { ref } from 'vue'
import { validateEntregaFile } from '@/utils/fileValidation'
import { entregaService } from '@/services/entregaService'
import { extractErrorMessage } from '@/composables/useAsync'
import ErrorMessage from '@/components/ErrorMessage/ErrorMessage.vue'

const props = defineProps({
  tesisId: { type: [String, Number], required: true },
})

const emit = defineEmits(['close', 'uploaded'])

const fileInput = ref(null)
const selectedFile = ref(null)
const clientError = ref(null)
const serverError = ref(null)
const uploading = ref(false)
const progress = ref(0)

function handleFileChange(event) {
  const file = event.target.files?.[0] ?? null
  selectedFile.value = file
  clientError.value = file ? validateEntregaFile(file) : null
  serverError.value = null
}

async function handleSubmit() {
  const validationError = validateEntregaFile(selectedFile.value)
  if (validationError) {
    clientError.value = validationError
    return
  }

  uploading.value = true
  serverError.value = null
  progress.value = 0

  try {
    await entregaService.upload(props.tesisId, selectedFile.value, (evt) => {
      if (evt.total) progress.value = Math.round((evt.loaded / evt.total) * 100)
    })
    emit('uploaded')
    emit('close')
  } catch (err) {
    serverError.value = extractErrorMessage(err)
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <div class="modal card" role="dialog" aria-modal="true" aria-label="Subir avance">
      <h2>Subir avance</h2>

      <ErrorMessage v-if="clientError" :message="clientError" />
      <ErrorMessage v-if="serverError" :message="serverError" />

      <div class="form-field">
        <label for="entrega-file">Archivo PDF (máximo 20MB)</label>
        <input
          id="entrega-file"
          ref="fileInput"
          type="file"
          accept="application/pdf"
          :disabled="uploading"
          @change="handleFileChange"
        />
      </div>

      <div v-if="uploading" class="progress">
        <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
      </div>

      <div class="modal-actions">
        <button type="button" class="secondary" :disabled="uploading" @click="emit('close')">
          Cancelar
        </button>
        <button type="button" :disabled="uploading || !selectedFile || !!clientError" @click="handleSubmit">
          {{ uploading ? 'Subiendo…' : 'Subir' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3);
  z-index: 100;
}

.modal {
  width: 100%;
  max-width: 420px;
}

.modal h2 {
  margin-top: 0;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-2);
  margin-top: var(--spacing-4);
}

.progress {
  height: 6px;
  background: var(--color-bg);
  border-radius: 999px;
  overflow: hidden;
  margin-top: var(--spacing-2);
}

.progress-bar {
  height: 100%;
  background: var(--color-primary);
  transition: width 0.1s linear;
}
</style>
